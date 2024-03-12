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
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import p4_aas.NetworkController.Utils.ApiEnum;
import p4_aas.Submodels.AbstractSubmodel;

import java.util.List;

public class NetworkInfrastructureSubmodel extends AbstractSubmodel {

    private NetworkInfrastructureLambda lambdaProvider;

    public NetworkInfrastructureSubmodel() {
        super();
        this.lambdaProvider = new NetworkInfrastructureLambda(this.getNetworkController());
    }

    @Override
    public List<Submodel> createSubmodel() {
		Submodel swPrograms = new Submodel();
        swPrograms.setIdShort("SwPrograms");

        swPrograms.addSubmodelElement(getRunningProgramSw1());
        swPrograms.addSubmodelElement(getRunningProgramSw2());

		return List.of(swPrograms);
	}

    private Property getRunningProgramSw1() {
        Property currentProgram = new Property("Sw2CurrentProgram", ValueType.String);

		AASLambdaPropertyHelper.setLambdaValue(currentProgram, () -> {
			return lambdaProvider.getCurrentProgram(ApiEnum.CURRENTPROGRAM_SW1.url);
		}, null);

        return currentProgram;
    }

    private Property getRunningProgramSw2() {
        Property currentProgram = new Property("Sw1CurrentProgram", ValueType.String);

		AASLambdaPropertyHelper.setLambdaValue(currentProgram, () -> {
			return lambdaProvider.getCurrentProgram(ApiEnum.CURRENTPROGRAM_SW2.url);
		}, null);

        return currentProgram;
    }


}