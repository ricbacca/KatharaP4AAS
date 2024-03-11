package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.function.Function;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

import p4_aas.NetworkController.NetworkController;

public class ControllerLambda {
    private NetworkController client;

    public ControllerLambda() {
        this.client = new NetworkController();
    }

    /**
     * 
     * @param url controller IP
     * @return actual firewall rules from controller
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getFirewallRules(String url) {
        return (args) -> {
            SubmodelElement[] res = client.getRules(url);
            return res;
        };
    }
}
