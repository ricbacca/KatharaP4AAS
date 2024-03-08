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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

import p4_aas.Submodels.Utils.SSHManager;

import java.util.Map;
import java.util.HashMap;

public class SSHController {
    private SSHManager sshManager = new SSHManager();
    private Map<Integer, Channel> controllerChannels = new HashMap<>();

    private final String SSH_CNT1 = "100.0.1.2";
    private final String SSH_CNT2 = "100.0.1.4";

    private final String FIREWALL = "ryu.app.rest_firewall";
    private final String SIMPLE_SWITCH = "ryu.app.simple_switch_13";
    private final String API_REST = "ryu.app.ofctl_rest --wsapi-port";
    private final String BASE_COMMAND = "ryu-manager --ofp-tcp-listen-port";
    private final String KILL_RYU = "pkill ryu-manager";

    public SSHController() {
        controllerChannels.put(1, sshManager.channelInit("shell", SSH_CNT1));
        controllerChannels.put(2, sshManager.channelInit("shell", SSH_CNT2));

        controllerChannels.values().forEach(ch -> {
            ch.setOutputStream(System.out);
            try {
                ch.connect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param switchNumber 1 or 2
     * @param isClosedController if closed controller has to be enabled
     * @param isSelectiveController if selective controller has to be enabled
     * @return selected Command to be executed on SSH connection to each Controller
     */
    private String selectCommand(int switchNumber, boolean isClosedController, boolean isSelectiveController) {
        int tcpPort = (switchNumber == 1 ? 6633 : 6653);
        int apiRestPort = (switchNumber == 1 ? 8080 : 9090);
        
        String controllerProgram = 
            (isSelectiveController || (isClosedController && switchNumber == 1)) ?
                FIREWALL : SIMPLE_SWITCH;

        return String.format("%s %d %s %s %d", 
            BASE_COMMAND, tcpPort, controllerProgram, API_REST, apiRestPort);
    }

    public void setSimpleControllers(boolean status) {
        if (status) {
            sshManager.executeCommandOnShell(selectCommand(1, false, false), controllerChannels.get(1));
            sshManager.executeCommandOnShell(selectCommand(2, false, false), controllerChannels.get(2));
        } else {
            killController();
        }
    }

    public void setClosedController(boolean status) {
        if (status) {
            sshManager.executeCommandOnShell(selectCommand(1, true, false), controllerChannels.get(1));
            sshManager.executeCommandOnShell(selectCommand(2, true, false), controllerChannels.get(2));
        } else {
            killController();
        }
    }

    public void setSelectiveController(boolean status) {
        if (status) {
            sshManager.executeCommandOnShell(selectCommand(1, false, true), controllerChannels.get(1));
            sshManager.executeCommandOnShell(selectCommand(2, false, true), controllerChannels.get(2));
        } else {
            killController();
        }
    }

    /**
     * Executes a Kill command for ryu manager apps on each Ryu Controller.
     */
    private void killController() {
        sshManager.executeSingleCommand(KILL_RYU, SSH_CNT1);
        sshManager.executeSingleCommand(KILL_RYU, SSH_CNT2);
    }
}