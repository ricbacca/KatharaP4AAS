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

package p4_aas.RyuController.Models;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
    String generation_id;
    String role;
    
    public String getGeneration_id() {
        return generation_id;
    }
    public void setGeneration_id(String generation_id) {
        this.generation_id = generation_id;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public SubmodelElement[] formatResult() {
        SubmodelElement role = new Property("Role", ValueType.String);
        SubmodelElement generationId = new Property("GenerationId", ValueType.Integer);

        role.setKind(ModelingKind.TEMPLATE);
        generationId.setKind(ModelingKind.TEMPLATE);

        role.setValue("Role: " + this.role);
        generationId.setValue("GenerationId: " + this.generation_id);

        return new SubmodelElement[] {
            role, generationId
        };
    }
}
