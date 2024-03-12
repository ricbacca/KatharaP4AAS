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

public enum ApiEnum {
    SW1("http://100.0.1.2:3333"),
    SW2("http://100.0.1.4:4444"),

    GETRULES_SW1(SW1.url + "/getRules?switch=s1"),

    GETRULES_SW2(SW2.url + "/getRules?switch=s2"),

    DELETERULES_SW1(SW1.url + "/removeRule?switch=s1&number="),

    DELETERULES_SW2(SW2.url + "/removeRule?switch=s2&number="),

    CURRENTPROGRAM_SW1(SW1.url + "/getCurrentProgram?switch=s1"),

    CURRENTPROGRAM_SW2(SW2.url + "/getCurrentProgram?switch=s2");

    public final String url;

    private ApiEnum(String url) {
        this.url = url;
    }
}
