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

package p4_aas.Submodels.NetworkInfrastructure;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;

import p4_aas.Submodels.AbstractSubmodel;

import java.util.List;
import java.util.Map;

public class NetworkInfrastructureSubmodel extends AbstractSubmodel {

    private NetworkInfrastructureLambda lambdaProvider;

    public NetworkInfrastructureSubmodel() {
        super();
        this.lambdaProvider = new NetworkInfrastructureLambda(this.getRyuController(), this.getUtils());
    }

    @Override
    public List<Submodel> createSubmodel() {
		Submodel switches = new Submodel();
        Submodel controllers = new Submodel();

		switches.setIdShort("Switches");
        switches.addSubmodelElement(switchesOperation());

        controllers.setIdShort("SelectControllers");
        controllers.addSubmodelElement(setOpenController());
        controllers.addSubmodelElement(setClosedController());
        controllers.addSubmodelElement(setSelectiveController());

		return List.of(switches, controllers);
	}

    private Operation switchesOperation() {
        Operation switches = new Operation("SwitchesInfo");
        switches.setOutputVariables(getUtils().getOperationVariables(2, "Output"));
        switches.setWrappedInvokable(lambdaProvider.getSwitchesNumber());

        return switches;
    }

    private Operation setOpenController() {
        Operation setController = new Operation("OpenController");
        setController.setDescription(new LangStrings("Italian", "Sw1 and Sw2 are simple switches."));
        setController.setInputVariables(getUtils().getCustomInputVariables(
            Map.of(
                "Controller", ValueType.Boolean
            )));

        setController.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        setController.setWrappedInvokable(lambdaProvider.setSimpleSwitchControllers());

        return setController;
    }

    private Operation setClosedController() {
        Operation setController = new Operation("ClosedController");
        setController.setDescription(new LangStrings("Italian", "Sw2 and Sw2 talks only with their hosts."));
        setController.setInputVariables(getUtils().getCustomInputVariables(
            Map.of(
                "Controller", ValueType.Boolean
            )));

        setController.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        setController.setWrappedInvokable(lambdaProvider.setClosedControllers());

        return setController;
    }

    private Operation setSelectiveController() {
        Operation setController = new Operation("SelectiveController");
        setController.setInputVariables(getUtils().getCustomInputVariables(Map.of(
            "Controller", ValueType.Boolean
            )));
        setController.setOutputVariables(getUtils().getOperationVariables(1, "Output"));
        setController.setWrappedInvokable(lambdaProvider.setSelectiveControllers());

        return setController;
    }
}