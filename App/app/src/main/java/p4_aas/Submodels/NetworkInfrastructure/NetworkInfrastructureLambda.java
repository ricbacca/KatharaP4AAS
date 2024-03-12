package p4_aas.Submodels.NetworkInfrastructure;

import p4_aas.NetworkController.NetworkController;

public class NetworkInfrastructureLambda {
    private NetworkController client;

    public NetworkInfrastructureLambda(NetworkController client) {
        this.client = client;
    }

    public String getCurrentProgram(String url) {
        return this.client.getCurrentProgram(url);
    }
}
