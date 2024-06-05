package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.*;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;

import p4_aas.NetworkController.NetworkController;
import p4_aas.NetworkController.Serialization.RuleDescribers;
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
            List<SubmodelElement> finalRes = new LinkedList<>();

            client.getRules(url).forEach(el -> {
                finalRes.add(this.createProperty("Rule" , el));
            });
            return finalRes.toArray(new SubmodelElement[finalRes.size()]);
        };
    }

    private SubmodelElement createProperty(String idShort, Object value) {
        SubmodelElement el = new Property(idShort, value);
        el.setKind(ModelingKind.TEMPLATE);
        return el;
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> deleteRules(String url) {
        return (args) -> {
            client.deleteRule(url + (BigInteger) args.get("ruleID").getValue());
            return new SubmodelElement[]{};
        };
    }

    /**
     * Initilizing createRules submodel with new Operations, 
     * after getting current (and updated) rule Describers from the P4 Program.
     * @param controllerId to switch this submodel refers
     * @param sub Submodel to be updated
     */
    public void getRuleDescribers(int controllerId, Submodel sub) {
        client.getRuleDescribers(
            controllerId == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url)
            .forEach(ruleDescriber -> {
                sub.addSubmodelElement(this.createNewRuleOperation(ruleDescriber, controllerId));
        });
    }

    /**
     * 
     * @param ruleDescriber
     * @param controllerId to which the created rule will refer to.
     * @return nee Operation for given ruleDescriber, 
     * with all correct input fields and descriptions
     */
    private Operation createNewRuleOperation(RuleDescribers ruleDescriber, int controllerId) {
        List<OperationVariable> inputVars = new LinkedList<>();

        ruleDescriber.getKeys().forEach(k -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(k.getName() + ":" + k.getMatchType(), ValueType.String)));
        });

        ruleDescriber.getActionParams().forEach(ac -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(ac.getName() + ":" + ac.getPattern(), ValueType.String)));
        });

        Operation op = new Operation(ruleDescriber.getTableName() + ":" + ruleDescriber.getActionName());
        op.setDescription(new LangStrings("English", "idTable=" + ruleDescriber.getTableId() + "&idAction=" + ruleDescriber.getActionId()));
        op.setInputVariables(inputVars);
        op.setWrappedInvokable(this.putRuleOnController(op, controllerId));

        return op;
    }

    // Lambda function used by Operation variables, to effectively put rules on P4 controller.
    private Function<Map<String, SubmodelElement>, SubmodelElement[]> putRuleOnController(Operation op, int controllerId) {
        return (args) -> {
            // (Key, Value) -> (InputField ID Short, InputField retrieved value)
            Map<String, String> inputValues = op
                .getInputVariables()
                .stream()
                .collect(Collectors.toMap(
                    (el) -> el.getValue().getIdShort(), 
                    (el) -> (String) args.get(el.getValue().getIdShort()).getValue()
                ));

            String URL = (controllerId == 1 ? 
                ApiEnum.ADDRULE_SW1.url + op.getDescription().get("English") : ApiEnum.ADDRULE_SW2.url + op.getDescription().get("English"));

            client.postRule(URL, inputValues);

            return new SubmodelElement[]{};
        };
    }

    public Function<Map<String, SubmodelElement>, SubmodelElement[]> refreshRules(int controllerId, Submodel createRules) {
        return (args) -> {
            // Removing OLD operation variables, used to insert rules into P4 Controller. 
            // These rule describers, never exists anymore on the respective controller.
            createRules.getOperations().values().forEach(op -> {
                createRules.deleteSubmodelElement(op.getIdShort());
            });
            
            getRuleDescribers(controllerId, createRules);
            return new SubmodelElement[]{};
        };
    }
}