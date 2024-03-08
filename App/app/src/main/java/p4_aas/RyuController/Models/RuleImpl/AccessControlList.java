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

package p4_aas.RyuController.Models.RuleImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessControlList {
    List<Rule> access_control_list = new ArrayList<>();

    public void setAccess_control_list(List<Rule> accessControlList) {
        this.access_control_list = accessControlList;
    }

    public List<Rules> getRules() {
        List<Rules> rls = new LinkedList<>();

        this.access_control_list.stream().
            forEach(el -> el.rules.forEach(elem -> rls.add(elem)));

        return rls;
    }

    private SubmodelElement createProperty(String idShort, Object value) {
        SubmodelElement el = new Property(idShort, value);
        el.setKind(ModelingKind.TEMPLATE);
        return el;
    }

    private SubmodelElement[] createResults(List<Object> rule_id, List<Object> priority, List<Object> dl_type, List<Object> nw_src, List<Object> nw_dst, List<Object> actions) {
        return new SubmodelElement[]{
            createProperty("Priority", "Priority: " + priority),
            createProperty("RuleId", "Rule Id: " + rule_id),
            createProperty("DlType", "Dl Type: " + dl_type),
            createProperty("NwSrc", "Src: " + nw_src),
            createProperty("NwDst", "Dst: " + nw_dst),
            createProperty("Actions", "Actions: " + actions)
        };
    }

    public SubmodelElement[] formatRules() {
        Map<String, List<Object>> finalResult = new HashMap<String, List<Object>>();

        for(Field f: Rules.class.getDeclaredFields()) {
            List<Optional<Object>> tempList = new LinkedList<>();
            for(Rules el: this.getRules()) {
                try {
                    tempList.add(Optional.ofNullable(f.get(el)));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            finalResult.put(f.getName(), 
                List.copyOf(tempList.stream().map(el -> el.orElse("No value")).collect(Collectors.toList())));

            tempList.clear();
        }
        
        return createResults(
            finalResult.get("rule_id"), 
            finalResult.get("priority"), 
            finalResult.get("dl_type"), 
            finalResult.get("nw_src"), 
            finalResult.get("nw_dst"),
            finalResult.get("actions"));
    }

    @Override
    public String toString() {
        return "Rules: [" + access_control_list + "]";
    }
}