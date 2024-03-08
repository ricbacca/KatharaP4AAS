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

package p4_aas.RyuController.Utils;

public enum ApiEnum {
    CNT1("http://100.0.1.2:3333"),
    CNT2("http://100.0.1.4:4444"),

    GETALLSWITCHES("/stats/switches"),

    GETAGGREGATEFLOWSTATS("/stats/aggregateflow/"),

    GETALLFLOWSTATS("/stats/flow/"),

    GETROLE("/stats/role/"),

    SETROLE("/stats/role"),

    FIREWALL_DEFAULT_DENY("/firewall/module/enable/000000000000000"),

    FIREWALL_DEFAULT_ACCEPT("/firewall/module/disable/000000000000000"),

    GETFIREWALLRULES("/firewall/rules/000000000000000");

    public final String url;

    private ApiEnum(String url) {
        this.url = url;
    }

    /**
     * Used to not repeat same String on enum elements
     * @param controllerId 1 or 2
     * @param element from this enum
     * @return String of enum + specific ID for selected controller
     */
    public static String getElement(Integer controllerId, ApiEnum element) {
        if (element.equals(GETALLSWITCHES) || element.equals(SETROLE))
            return (controllerId == 1 ? CNT1.url : CNT2.url) + element.url;
        else
            return (controllerId == 1 ? CNT1.url : CNT2.url) + element.url + controllerId;
    }
}
