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

package p4_aas;

/**
 * To manage System properties, used to mantain info throughout the app
 * about which controller is on, and not cause any problems.
 */
public class StaticProperties {

    private static final String SIMPLE_CONTROLLERS = "SIMPLECONTROLLERS";
    private static final String CLOSED_CONTROLLERS = "CLOSEDCONTROLLERS";
    private static final String SELECTIVE_CONTROLLERS = "SELECTIVECONTROLLERS";

    private static boolean getStatus(String CONTROLLER) {
        return Boolean.parseBoolean(System.getProperty(CONTROLLER, "false"));
    }

    private static void setStatus(String CONTROLLER, boolean newStatus) {
        System.out.println("" +     CONTROLLER + ": " + newStatus);
        System.setProperty(CONTROLLER,  Boolean.toString(newStatus));
    }

    public static boolean isSimpleControllers() {
        return getStatus(SIMPLE_CONTROLLERS);
    }

    public static boolean isClosedControllers() {
        return getStatus(CLOSED_CONTROLLERS);
    }

    public static boolean isSelectiveControllers() {
        return getStatus(SELECTIVE_CONTROLLERS);
    }

    public static void setSimpleControllers(Boolean newValue) {
        setStatus(SIMPLE_CONTROLLERS, newValue);
    }

    public static void setClosedControllers(Boolean newValue) {
        setStatus(CLOSED_CONTROLLERS, newValue);
    }

    public static void setSelectiveControllers(Boolean newValue) {
        setStatus(SELECTIVE_CONTROLLERS, newValue);
    }
}