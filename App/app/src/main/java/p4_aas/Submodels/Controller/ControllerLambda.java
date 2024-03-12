package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.function.Function;
import java.math.*;

import org.apache.http.client.HttpResponseException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

import p4_aas.NetworkController.NetworkController;

public class ControllerLambda {
    private NetworkController client;

    public ControllerLambda(NetworkController client) {
        this.client = client;
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getRules(String url) {
        return (args) -> {
            return client.getRules(url);
        };
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> deleteRules(String url) {
        return (args) -> {
            try {
                client.deleteRule(url + (BigInteger) args.get("ruleID").getValue());
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }
            return new SubmodelElement[]{};
        };
    }
}
