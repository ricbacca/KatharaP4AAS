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

    public String getRequest(String url) {
        return this.client.getRequest(url);
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> changeProgram() {
       return (args) -> {
            Integer Switch = ((BigInteger) args.get("Switch").getValue()).intValue();
            if (Switch == 1) {
                client.getRequest(ApiEnum.CHANGEPROGRAM_SW1.url + (String) args.get("Program").getValue());
            }
            if (Switch == 2) {
                client.getRequest(ApiEnum.CHANGEPROGRAM_SW2.url + (String) args.get("Program").getValue());
            }

            return new SubmodelElement[]{};
        };
    }

}
