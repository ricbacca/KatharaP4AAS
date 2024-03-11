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

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;

import p4_aas.AssetShells.IShell;
import p4_aas.AssetShells.Machine;
import p4_aas.AssetShells.NetworkControlPlane;
import p4_aas.AssetShells.NetworkInfrastructure;
import p4_aas.NetworkController.NetworkController;

import java.util.List;

public class App {
    private final static String manual = "https://www.plattform-i40.de/IP/Redaktion/EN/Downloads/Publikation/Details_of_the_Asset_Administration_Shell_Part1_V3.pdf?__blob=publicationFile&v=1";
    private final static String version = "1.0.0";
    private final static NetworkController client = new NetworkController();
    public static void main(String[] args) {
        waitForRegistry();

        IShell networkInfrastructure = new NetworkInfrastructure(
            6001, 
            "Network Infrastructure", 
            "org.unibo.aas.networkInfrastructure", 
            AssetKind.INSTANCE);

        IShell networkControlPlane = new NetworkControlPlane(
            6002, 
            "Network Control Plane", 
            "org.unibo.aas.networkControlPlane", 
            AssetKind.INSTANCE);

        IShell machineOne = new Machine(
            6003,
            "Machine1",
            "org.unibo.aas.machineOne",
            AssetKind.INSTANCE,
            manual,
            version,
            List.of("10.0.1.1", "10.0.1.2"));

        IShell machineTwo = new Machine(
            6004,
            "Machine2",
            "org.unibo.aas.machineTwo",
            AssetKind.INSTANCE,
            manual,
            version,
            List.of("10.0.2.1", "10.0.2.2"));

        IShell machineThree = new Machine(
            6005,
            "Machine3",
            "org.unibo.aas.machineThree",
            AssetKind.INSTANCE,
            manual,
            version,
            List.of("10.0.3.1", "10.0.3.2"));

        networkInfrastructure.createAndStartServlet();
        networkControlPlane.createAndStartServlet();
        machineOne.createAndStartServlet();
        machineTwo.createAndStartServlet();
        machineThree.createAndStartServlet();
    }

    /**
     * Polling on Registry Url, waiting for a positive response to proceed on.
     */
    
    private static void waitForRegistry() {
        String URL = "http://100.0.2.1:4000/registry/api/v1/registry";

        System.out.print("Waiting for Registry at 100.0.2.1:4000");
        while(!client.isServerAvailable(URL)) {
            System.out.print(".");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}