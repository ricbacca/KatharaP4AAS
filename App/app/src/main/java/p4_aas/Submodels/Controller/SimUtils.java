package p4_aas.Submodels.Controller;

import org.apache.http.client.HttpResponseException;

import p4_aas.RyuController.Controller;
import p4_aas.RyuController.Utils.ApiEnum;

// Copyright 2024 riccardo.bacca@studio.unibo.it
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

public class SimUtils {

    private static final String ALLOWRULE = "ALLOW";
    private static final String DENYRULE = "DENY";
    private static final Integer MAXPRIORITY = 10;
    private static final Integer MINPRIORITY = 1;

    String cnt1 = ApiEnum.getElement(1, ApiEnum.GETFIREWALLRULES);
    String cnt2 = ApiEnum.getElement(2, ApiEnum.GETFIREWALLRULES);


    public void makeFirewallDefaultAcceptance(Controller client) {
        try {
            client.postFirewallRules(cnt1, "", "", ALLOWRULE, MINPRIORITY);
            client.postFirewallRules(cnt2, "", "", ALLOWRULE, MINPRIORITY);    
        } catch (HttpResponseException e) {
            e.printStackTrace();
        }        
    }

    public void manageTrafficToHost(Controller client, String hostIP, Boolean allow) {
        try {
            client.postFirewallRules(cnt1, hostIP, "", allow ? ALLOWRULE : DENYRULE, allow ? MINPRIORITY : MAXPRIORITY);    
            client.postFirewallRules(cnt1, "", hostIP, allow ? ALLOWRULE : DENYRULE, allow ? MINPRIORITY : MAXPRIORITY);
            client.postFirewallRules(cnt2, hostIP, "", allow ? ALLOWRULE : DENYRULE, allow ? MINPRIORITY : MAXPRIORITY);    
            client.postFirewallRules(cnt2, "", hostIP, allow ? ALLOWRULE : DENYRULE, allow ? MINPRIORITY : MAXPRIORITY);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        }     
    }
    
}