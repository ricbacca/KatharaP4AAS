package p4_aas.Submodels.NetworkInfrastructure;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.apache.http.client.HttpResponseException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import p4_aas.StaticProperties;
import p4_aas.RyuController.Controller;
import p4_aas.RyuController.Utils.ApiEnum;
import p4_aas.Submodels.Utils.Utils;

enum Switch {SIMPLE, CLOSED, SELECTIVE};

public class NetworkInfrastructureLambda {

    private Utils utils;
    private SSHController sshUtils;
    private Controller ryuController;
    
    public NetworkInfrastructureLambda(Controller ryuController, Utils utils) {
        this.utils = utils;
        this.ryuController = ryuController;
        this.sshUtils = new SSHController();
    }

    /**
     * @param controllerInput true or false
     * @param switchType SimpleSwitch, Closed or Firewall
     * Checks env variables to know if new controller can be enabled or not
     * In negative cases, an exception is thrown.
     */
    private void checkEnvironment(boolean controllerInput, Switch switchType) {
        boolean closedController = StaticProperties.isClosedControllers();
        boolean simpleController = StaticProperties.isSimpleControllers();
        boolean selectiveController = StaticProperties.isSelectiveControllers();

        boolean throwException = false;

        switch (switchType) {
            case SIMPLE:
                if ((simpleController && controllerInput) || (!simpleController && !controllerInput) || (closedController || selectiveController))
                    throwException = true;
                break;
            case CLOSED:
                if ((closedController && controllerInput) || (!closedController && !controllerInput) || (simpleController || selectiveController))
                    throwException = true;
                break;
            case SELECTIVE:
                if ((selectiveController && controllerInput) || (!selectiveController && !controllerInput) || (simpleController || closedController))
                    throwException = true;
                break;
            default:
                throwException = false;
                break;
        }

        if (throwException)
            throw new IllegalArgumentException("Problem with controller status !");
    }

    /**
     * 
     * @return actual switch and Controllers status, 
     * to know which switch is connected to which controller and viceversa
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getSwitchesNumber() {
        return (args) -> {
            SubmodelElement cnt1 = new Property("Controller1", ValueType.String);
            SubmodelElement cnt2 = new Property("Controller2", ValueType.String);

            String cnt1Status = null;
            String cnt2Status = null;

            try {
                cnt1Status = ryuController.makeRequestWithoutSerialize(ApiEnum.getElement(1, ApiEnum.GETALLSWITCHES));
                cnt2Status = ryuController.makeRequestWithoutSerialize(ApiEnum.getElement(2, ApiEnum.GETALLSWITCHES));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            if (cnt1Status == null) {
                cnt1.setValue("Controller 1: disabled");
            } else {
                cnt1.setValue("Controller 1: connected to Switch " + cnt1Status);
            }

            if (cnt2Status == null) {
                cnt2.setValue("Controller 2: disabled");
            } else {
                cnt2.setValue("Controller 2: connected to Switch " + cnt2Status);
            }

            return new SubmodelElement[] {
                cnt1, cnt2
            };
        };
    }

    /**
     * To start up simple switch controllers on each network switch.
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> setSimpleSwitchControllers() {
        return (args) -> {
            Boolean controllerStatus = utils.getOrElse(args.get("Controller").getValue());

            checkEnvironment(controllerStatus, Switch.SIMPLE);

            sshUtils.setSimpleControllers(controllerStatus);
            StaticProperties.setSimpleControllers(controllerStatus);
            return new SubmodelElement[] {
                new Property(controllerStatus ? "Activated" : "Deactivated"),
            };
        };
    }

    /**
     * To start up Firewall controllers on each network switch.
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> setClosedControllers() {
        return (args) -> {
            Boolean controllerStatus = utils.getOrElse(args.get("Controller").getValue());

            checkEnvironment(controllerStatus, Switch.CLOSED);

            sshUtils.setClosedController(controllerStatus);
            if (controllerStatus)
                this.firewallDefaultDenyEnable(false);
            StaticProperties.setClosedControllers(controllerStatus);
            
            return new SubmodelElement[] {
                new Property(controllerStatus ? "Activated" : "Deactivated"),
            };
        };
    }

    /**
     * To start up Firewall controllers on each network switch.
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> setSelectiveControllers() {
        return (args) -> {
            Boolean controllerStatus = utils.getOrElse(args.get("Controller").getValue());

            checkEnvironment(controllerStatus, Switch.SELECTIVE);

            sshUtils.setSelectiveController(controllerStatus);
            
            if (controllerStatus) {
                this.firewallDefaultDenyEnable(true);
            }

            StaticProperties.setSelectiveControllers(controllerStatus);

            return new SubmodelElement[] {
                new Property(controllerStatus ? "Activated" : "Deactivated"),
            };

        };
    }

    /**
     * Enables Firewall Controllers (to be Default Deny) on each network switch
     * @param bothSwitches if has to be enabled Default Deny mode on all switches
     */
    private void firewallDefaultDenyEnable(boolean bothSwitches) {
        this.waitForFirewallToStart();

        if (bothSwitches) {
            try {
                ryuController.putRequest(ApiEnum.getElement(2, ApiEnum.FIREWALL_DEFAULT_DENY));
                ryuController.putRequest(ApiEnum.getElement(1, ApiEnum.FIREWALL_DEFAULT_DENY));
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForFirewallToStart() {
        System.out.print("Waiting for Firewalls to start-up");
        while(!ryuController.isServerAvailable(ApiEnum.getElement(1, ApiEnum.GETFIREWALLRULES))
            || !ryuController.isServerAvailable(ApiEnum.getElement(2, ApiEnum.GETFIREWALLRULES))) {
            System.out.print(".");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
