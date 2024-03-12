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
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;

import p4_aas.NetworkController.Utils.ApiEnum;
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
        this.lambdaProvider = new ControllerLambda(this.getNetworkController());
    }

    @Override
    public List<Submodel> createSubmodel() {
        Submodel cntSubmodel = new Submodel();

		cntSubmodel.setIdShort("Controller" + controllerId);

        cntSubmodel.addSubmodelElement(getRules());
        cntSubmodel.addSubmodelElement(deleteRules());

		return List.of(cntSubmodel);
    }

    private Operation getRules() {
        Operation getRules = new Operation("GetSwitchRules");
        getRules.setOutputVariables(getUtils().getOperationVariables(Utils.GET_FIREWALL_RULES, "Output"));

        getRules.setWrappedInvokable(lambdaProvider.getRules(controllerId == 1 ? ApiEnum.GETRULES_SW1.url :
                                                                                            ApiEnum.GETRULES_SW2.url));
        return getRules;
    }

    private Operation deleteRules() {
        Operation deleteRules = new Operation("DeleteSwitchRules");
        deleteRules.setInputVariables(getUtils().getCustomInputVariables(Map.of("ruleID", ValueType.Integer)));

        deleteRules.setWrappedInvokable(lambdaProvider.deleteRules(controllerId == 1 ? ApiEnum.DELETERULES_SW1.url :
                                                                                          ApiEnum.DELETERULES_SW2.url));
        return deleteRules;
    }
}