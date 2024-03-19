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

package p4_aas.NetworkController.Utils;

import p4_aas.StaticProperties;

public enum ApiEnum {
    GETRULES_SW1(StaticProperties.SW1_IP + "/getRules?switch=s1"),

    GETRULES_SW2(StaticProperties.SW2_IP + "/getRules?switch=s2"),

    DELETERULES_SW1(StaticProperties.SW1_IP + "/removeRule?switch=s1&number="),

    DELETERULES_SW2(StaticProperties.SW2_IP + "/removeRule?switch=s2&number="),

    CURRENTPROGRAM_SW1(StaticProperties.SW1_IP + "/getCurrentProgram?switch=s1"),

    CURRENTPROGRAM_SW2(StaticProperties.SW2_IP + "/getCurrentProgram?switch=s2"),
    
    AVAILABLEPROGRAMS_SW1(StaticProperties.SW1_IP +  "/getAvailablePrograms"),

    AVAILABLEPROGRAMS_SW2(StaticProperties.SW2_IP +  "/getAvailablePrograms"),

    CHANGEPROGRAM_SW1(StaticProperties.SW1_IP + "/executeProgram?switch=s1&program="),

    CHANGEPROGRAM_SW2(StaticProperties.SW2_IP + "/executeProgram?switch=s2&program="),

    RULEDESCRIBER_SW1(StaticProperties.SW1_IP + "/getRuleDescriber?switch=s1"),

    RULEDESCRIBER_SW2(StaticProperties.SW2_IP + "/getRuleDescriber?switch=s2");

    public final String url;

    private ApiEnum(String url) {
        this.url = url;
    }
}
