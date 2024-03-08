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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rules {
    int rule_id;
    int priority;
    String dl_type;
    String nw_src;
    String nw_dst;
    String actions;

    public int getRule_id() {
        return rule_id;
    }
    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public String getDl_type() {
        return dl_type;
    }
    public void setDl_type(String dl_type) {
        this.dl_type = dl_type;
    }
    public String getNw_src() {
        return nw_src;
    }
    public void setNw_src(String nw_src) {
        this.nw_src = nw_src;
    }
    public String getNw_dst() {
        return nw_dst;
    }
    public void setNw_dst(String nw_dst) {
        this.nw_dst = nw_dst;
    }
    public String getActions() {
        return actions;
    }
    public void setActions(String actions) {
        this.actions = actions;
    }
    @Override
    public String toString() {
        return "Rules [rule_id=" + rule_id + ", priority=" + priority + ", dl_type=" + dl_type + ", nw_src="
                + nw_src + ", nw_dst=" + nw_dst + ", actions=" + actions + "]";
    }
}
