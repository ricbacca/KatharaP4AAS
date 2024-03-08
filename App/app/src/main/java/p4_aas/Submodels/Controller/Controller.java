// Copyright 2023 riccardo.bacca@studio.unibo.it
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package p4_aas.Submodels.Controller;

import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;

import p4_aas.RyuController.Utils.ApiEnum;
import p4_aas.Submodels.AbstractSubmodel;
import p4_aas.Submodels.Utils.Utils;

/**
 * Submodel Impl for Netwok Control Plane AAS.
 */
public class Controller extends AbstractSubmodel {

    private ControllerLambda lambdaProvider;
    private final int controllerId;

    public Controller(int controllerNumber) {
        super();
        this.controllerId = controllerNumber;
        this.lambdaProvider = new ControllerLambda();
    }

    @Override
    public List<Submodel> createSubmodel() {
        Submodel cntSubmodel = new Submodel();
        Submodel simulations = new Submodel();

		cntSubmodel.setIdShort("Controller" + controllerId);
        simulations.setIdShort("Simulations");

        cntSubmodel.addSubmodelElement(aggregateFlowStats());
        cntSubmodel.addSubmodelElement(allFlowStats());
        cntSubmodel.addSubmodelElement(getRole());
        cntSubmodel.addSubmodelElement(setRole());
        cntSubmodel.addSubmodelElement(setFirewallRules());
        cntSubmodel.addSubmodelElement(getFirewallRules());
        cntSubmodel.addSubmodelElement(deleteFirewallRule());

        simulations.addSubmodelElement(isolateSingleHost());
        simulations.addSubmodelElement(enableSingleHostTraffic());
        simulations.addSubmodelElement(excludeExternalAccess());

		return List.of(cntSubmodel, simulations);
    }

    private Operation aggregateFlowStats() {
        Operation aggregateFlows = new Operation("AggregateFlows");
        aggregateFlows.setOutputVariables(getUtils().getOperationVariables(Utils.AGGREGATE_FLOWS_OUTPUT, "Output"));
        aggregateFlows.setWrappedInvokable(
            lambdaProvider.getAggregateStats(ApiEnum.getElement(controllerId, ApiEnum.GETAGGREGATEFLOWSTATS)));

        return aggregateFlows;
    }

    private Operation allFlowStats() {
        Operation allFlowStats = new Operation("AllFlowStats");

        allFlowStats.setOutputVariables(getUtils().getOperationVariables(Utils.ALL_FLOWS_OUTPUT, "Output"));
        allFlowStats.setWrappedInvokable(
            lambdaProvider.getAllFlowStats(ApiEnum.getElement(controllerId, ApiEnum.GETALLFLOWSTATS)));

        return allFlowStats;
    }

    private Operation getRole() {
        Operation getRole = new Operation("GetRole");

        getRole.setOutputVariables(getUtils().getOperationVariables(Utils.GET_ROLE_OUTPUT, "Output"));
        getRole.setWrappedInvokable(
            lambdaProvider.getRole(ApiEnum.getElement(controllerId, ApiEnum.GETROLE)));

        return getRole;
    }

    private Operation setRole() {
        Operation setRole = new Operation("SetRole");
        setRole.setInputVariables(getUtils().getCustomInputVariables(
            Map.of("Role", ValueType.String)
        ));
        setRole.setOutputVariables(getUtils().getOperationVariables(Utils.SET_ROLE_OUTPUT, "Output"));
        setRole.setWrappedInvokable(
            lambdaProvider.setRole(ApiEnum.getElement(controllerId, ApiEnum.SETROLE)));

        return setRole;
    }

    private Operation setFirewallRules() {
        Operation setRule = new Operation("SetFirewallRules");
        setRule.setDescription(new LangStrings("English", "Source and Destination between 1 and 6. Add rules for both ways."));
        setRule.setInputVariables(getUtils().getCustomInputVariables(Map.of(
            "Source", ValueType.String,
            "Destination", ValueType.String,
            "Type", ValueType.String,
            "Priority", ValueType.Integer
        )));
        setRule.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        setRule.setWrappedInvokable(lambdaProvider.setFirewallRule(this.getFirewallUrl()));

        return setRule;
    }

    private Operation getFirewallRules() {
        Operation getRules = new Operation("GetFirewallRules");
        getRules.setOutputVariables(getUtils().getOperationVariables(Utils.GET_FIREWALL_RULES, "Output"));

        getRules.setWrappedInvokable(lambdaProvider.getFirewallRules(this.getFirewallUrl()));

        return getRules;
    }

    private Operation deleteFirewallRule() {
        Operation deleteRule = new Operation("DeleteFirewallRules");
        deleteRule.setInputVariables(getUtils().getCustomInputVariables(Map.of("RuleId", ValueType.String)));
        deleteRule.setOutputVariables(getUtils().getOperationVariables(1, "Output"));

        deleteRule.setWrappedInvokable(lambdaProvider.deleteFirewallRules(this.getFirewallUrl()));

        return deleteRule;
    }

    private Operation isolateSingleHost() {
        Operation isolateSingleHost = new Operation("IsolateSingleHost");
        isolateSingleHost.setDescription(new LangStrings("English", "Single given host will be banned to/from traffic."));
        isolateSingleHost.setInputVariables(getUtils().getCustomInputVariables(Map.of(
            "HostIP", ValueType.String
        )));
        isolateSingleHost.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        isolateSingleHost.setWrappedInvokable(lambdaProvider.manageHostReachability(ControllerLambda.simulationType.DENYSINGLEHOST));

        return isolateSingleHost;
    }

    private Operation enableSingleHostTraffic() {
        Operation enableSingleHost = new Operation("EnableSingleHost");
        enableSingleHost.setDescription(new LangStrings("English", "Single given host will be banned to/from traffic."));
        enableSingleHost.setInputVariables(getUtils().getCustomInputVariables(Map.of(
            "HostIP", ValueType.String
        )));
        enableSingleHost.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        enableSingleHost.setWrappedInvokable(lambdaProvider.manageHostReachability(ControllerLambda.simulationType.ALLOWSINGLEHOST));

        return enableSingleHost;
    }

    private Operation excludeExternalAccess() {
        Operation excludeAccess = new Operation("ExcludeExternalAccess");
        excludeAccess.setDescription(new LangStrings("English", "External hosts (domain 192.168.0.X/24) will be banned."));
        excludeAccess.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        excludeAccess.setWrappedInvokable(lambdaProvider.manageHostReachability(ControllerLambda.simulationType.DENYEXTERNALHOST));

        return excludeAccess;
    }


    private String getFirewallUrl() {
        return ApiEnum.getElement(controllerId, ApiEnum.GETFIREWALLRULES);
    }
}