package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
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
    private List<String> keys;

    public List<String> getKeys() {
        return keys;
    }

    public ControllerLambda(NetworkController client, Utils utils) {
        this.client = client;
        this.utils = utils;
        this.keys = new LinkedList<>();
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

    public List<SwitchDescribers> getRuleDescribers(String URL) {
        try {
            return client.getRuleDescribers(URL);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Submodel initCreateRules(int controllerId) {
        Submodel sub = new Submodel();
        sub.setIdShort("CreateRules");

        List<SwitchDescribers> ruleDescribers = getRuleDescribers(controllerId == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url);
        this.keys = ruleDescribers.stream().map(el -> el.getTableName() + ":" + el.getActionName()).toList();
        ruleDescribers.forEach(el -> {
            sub.addSubmodelElement(this.createNewRuleOperation(el));
        });

        return sub;
    }

    public Operation createNewRuleOperation(SwitchDescribers sd) {
        List<OperationVariable> inputVars = new LinkedList<>();
        Operation op = new Operation(sd.getTableName() + ":" + sd.getActionName());
        op.setDescription(new LangStrings("English", "idTable=" + sd.getTableId() + "&idAction=" + sd.getActionId()));

        sd.getKeys().forEach(k -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(k.getName() + ":" + k.getMatchType(), ValueType.String)));
        });

        sd.getActionParams().forEach(ac -> {
            inputVars.addAll(utils.getCustomInputVariables(Map.of(ac.getName() + ":" + ac.getPattern(), ValueType.String)));
        });

        op.setInputVariables(inputVars);

        return op;
    }

    public void refreshRuleSubmodel(int controllerId, Submodel createRules) {
        List<SwitchDescribers> ruleDescribers = getRuleDescribers(controllerId == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url);
        this.keys = ruleDescribers.stream().map(el -> el.getTableName() + ":" + el.getActionName()).toList();
        ruleDescribers.forEach(el -> {
            createRules.addSubmodelElement(this.createNewRuleOperation(el));
        });
    }
}
