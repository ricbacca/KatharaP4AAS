package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.*;

import org.apache.http.client.HttpResponseException;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;

import p4_aas.NetworkController.NetworkController;
import p4_aas.NetworkController.SwitchDescribers;
import p4_aas.NetworkController.Utils.ApiEnum;
import p4_aas.Submodels.Utils.Utils;

public class ControllerLambda {
    private NetworkController client;
    private Utils utils;

    public ControllerLambda(NetworkController client, Utils utils) {
        this.client = client;
        this.utils = utils;
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

    public List<SwitchDescribers> getRuleDescribersForSwitch(String URL) {
        try {
            return client.getRuleDescribers(URL);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Initializing Submodel and Op Variables to create new Controller rules
    public Submodel initCreateRules(int controllerId) {
        Submodel sub = new Submodel();
        sub.setIdShort("CreateRules");

        getRuleDescribersForSwitch(controllerId == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url).forEach(el -> {
            sub.addSubmodelElement(this.createNewRuleOperation(el, controllerId));
        });

        return sub;
    }

    // Controller rules - op variables creation
    public Operation createNewRuleOperation(SwitchDescribers sd, int controllerId) {
        List<OperationVariable> inputVars = new LinkedList<>();

        sd.getKeys().forEach(k -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(k.getName() + ":" + k.getMatchType(), ValueType.String)));
        });

        sd.getActionParams().forEach(ac -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(ac.getName() + ":" + ac.getPattern(), ValueType.String)));
        });

        Operation op = new Operation(sd.getTableName() + ":" + sd.getActionName());
        op.setDescription(new LangStrings("English", "idTable=" + sd.getTableId() + "&idAction=" + sd.getActionId()));
        op.setInputVariables(inputVars);
        op.setWrappedInvokable(this.putRuleOnController(op, controllerId));

        return op;
    }

    private Function<Map<String, SubmodelElement>, SubmodelElement[]> putRuleOnController(Operation op, int controllerId) {
        return (args) -> {
            // (Key, Value) -> (InputField ID Short, InputField retrieved value)
            Map<String, String> inputValues = op.getInputVariables()
                .stream()
                .collect(Collectors.toMap(
                    (el) -> el.getValue().getIdShort(), 
                    (el) -> (String) args.get(el.getValue().getIdShort()).getValue()
                ));

            String URL = (controllerId == 1 ? 
                ApiEnum.ADDRULE_SW1.url + op.getDescription().get("English") : ApiEnum.ADDRULE_SW2.url + op.getDescription().get("English"));

            try {
                client.postRule(URL, inputValues);
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }

            return new SubmodelElement[]{};
        };
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> refreshRules(int controllerId, Submodel createRules) {
        return (args) -> {
            // Removing OLD operation variables
            createRules.getOperations().values().forEach(op -> {
                createRules.deleteSubmodelElement(op.getIdShort());
            });
            
            // Updating OperationVariables
            getRuleDescribersForSwitch(controllerId == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url).forEach(sd -> {
                createRules.addSubmodelElement(this.createNewRuleOperation(sd, controllerId));
            });
            return new SubmodelElement[]{};
        };
    }
}
