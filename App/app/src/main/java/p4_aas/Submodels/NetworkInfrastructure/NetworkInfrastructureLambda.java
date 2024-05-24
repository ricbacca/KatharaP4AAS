package p4_aas.Submodels.NetworkInfrastructure;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import p4_aas.NetworkController.NetworkController;
import p4_aas.NetworkController.Utils.ApiEnum;

public class NetworkInfrastructureLambda {
    private NetworkController client;

    public NetworkInfrastructureLambda(NetworkController client) {
        this.client = client;
    }

    /**
     * Used into getting current program of specified Switch.
     * @return lambda function for Submodel Operations
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getRequest() {
        return (args) -> {
            String result = "null";
            Integer Switch = ((BigInteger) args.get("Switch").getValue()).intValue();

            if (Switch == 1)
                result = this.client.getRequest(ApiEnum.CURRENTPROGRAM_SW1.url);
            else if (Switch == 2)
                result = this.client.getRequest(ApiEnum.CURRENTPROGRAM_SW2.url);

            
            return new SubmodelElement[]{
                new Property(result)
            };
        };
    }

    /**
     * Used into Operation to change executed program in specified P4 Switch.
     * @return lambda function.
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> changeProgram() {
       return (args) -> {
            this.changeSwitchProgram(this.getSwitchID(args), this.getProgram(args));
            this.postRules(this.getSwitchID(args), this.getProgram(args), this.getHost(args));

            return new SubmodelElement[]{};
        };
    }

    private void postRules(int switchID, String program, String host) {
        switch(switchID) {
            case 1: 
                this.postArpRules(switchID, ApiEnum.ARP_REPLY_RULE_SW1.url);
                switch(program) {
                    case "standard": break;
                    case "blockSingleHost": break;
                    case "enableSingleHost": break;
                    default: break;
                }
                break;
            case 2: 
                this.postArpRules(switchID, ApiEnum.ARP_REPLY_RULE_SW2.url);
                switch(program) {
                    case "standard": break;
                    case "blockSingleHost": break;
                    case "enableSingleHost": break;
                    default: break;
                }
                break;
        }
    }

    private void postArpRules(int switchID, String URL) {
        switch(switchID) {
            case 1:
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.1.1",
                    "src_mac", "00:00:00:00:00:01"     
                ));
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.1.2",
                    "src_mac", "00:00:00:00:00:01"
                ));
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.2.1",
                    "src_mac", "00:00:00:00:00:02"     
                ));
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.2.2",
                    "src_mac", "00:00:00:00:00:02"
                ));
                break;
            case 2:
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.3.1",
                    "src_mac", "00:00:00:00:00:04"     
                ));
                client.postRule(URL, Map.of(
                    "hdr.arp.dstAddr", "10.0.3.2",
                    "src_mac", "00:00:00:00:00:04"
                ));
                break;
            default:
                break;
        }
    }

    private int getSwitchID(Map<String, SubmodelElement> args) {
        return ((BigInteger) args.get("Switch").getValue()).intValue();
    }

    private String getProgram(Map<String, SubmodelElement> args) {
        return (String) args.get("Program").getValue();
    }

    private String getHost(Map<String, SubmodelElement> args) {
        return (String) args.get("Host").getValue();
    }

    private void changeSwitchProgram(int switchId, String program) {
        String URL = (switchId == 1 ?
            ApiEnum.CHANGEPROGRAM_SW1.url :
            ApiEnum.CHANGEPROGRAM_SW2.url) + program;
        client.getRequest(URL);
    }

}
