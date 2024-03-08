package p4_aas.Submodels.Controller;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.http.client.HttpResponseException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.joda.time.IllegalFieldValueException;

import com.fasterxml.jackson.core.type.TypeReference;

import p4_aas.RyuController.Controller;
import p4_aas.RyuController.Models.AggregateFlowStats;
import p4_aas.RyuController.Models.AllFlowStats;
import p4_aas.RyuController.Models.Role;
import p4_aas.RyuController.Models.RoleEnum;
import p4_aas.RyuController.Models.RuleImpl.AccessControlList;

public class ControllerLambda {
    enum firewallRulesType {ALLOW, DENY};
    enum simulationType {ALLOWSINGLEHOST, DENYSINGLEHOST, DENYEXTERNALHOST};

    private Controller client;
    private SimUtils simulation;

    public ControllerLambda() {
        this.client = new Controller();
        this.simulation = new SimUtils();
    }

    /**
     * @param url controller IP
     * @return Aggregate Stats from Controller at given URL
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getAggregateStats(String url) {
        return (args) -> {
            Map<String, AggregateFlowStats[]> res = client.makeRequestWithSerialization(url, new TypeReference<Map<String, AggregateFlowStats[]>>(){});
            return res.values().stream().findFirst().get()[0].formatResult();
        };
    }

    /**
     * 
     * @param url controller IP
     * @return All Flow Stats for given Controller IP
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getAllFlowStats(String url) {
        return (args) -> {
            Map<String, AllFlowStats[]> res = client.makeRequestWithSerialization(
                url, 
                new TypeReference<Map<String, AllFlowStats[]>>(){});
            
            return AllFlowStats.formatMultipleResults(res.values().stream().collect(Collectors.toList()));
        };
    }

    /**
     * 
     * @param url for controller
     * @return actual Role for given Controller IP
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getRole(String url) {
        return (args) -> {
            Map<String, Role[]> res = client.makeRequestWithSerialization(
                url, 
                new TypeReference<Map<String, Role[]>>(){});
            
            return res.values().stream().findFirst().get()[0].formatResult();
        };
    }

    /**
     * 
     * @param url controller IP
     * @return Sets a new Role (got from Input Field) for controller IP
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> setRole(String url) {
        return (args) -> {
            String input = (String) args.get("Role").getValue();

            if (EnumUtils.isValidEnum(RoleEnum.class, input)) {
                try {
                    client.setControllerRole(url, 1, RoleEnum.valueOf(input).toString());
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                }
            }
            
            return new SubmodelElement[] {
                new Property("All OK")
            }; 
        }; 
    }

    /**
     * @param url Controller IP on which to create the new Firewall Rule
     * @return Sets a new Firewall Rule with given Source and Destination IP
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> setFirewallRule(String url) {
        return (args) -> {
            String src = args.get("Source").getValue() == null ? "" : args.get("Source").getValue().toString();
            String dst = args.get("Destination").getValue() == null ? "" : args.get("Destination").getValue().toString();
            String type = args.get("Type").getValue().toString();
            int priority = args.get("Priority").getValue() == null ? 1 : Integer.valueOf(args.get("Priority").getValue().toString());

            if (!EnumUtils.isValidEnum(firewallRulesType.class, type)){
                throw new IllegalFieldValueException("Type", type);
            }
            
            try {
                client.postFirewallRules(url, src, dst, type, priority);
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }

            return new SubmodelElement[] {
                new Property("All OK")
            };
        };
    }

    /**
     * 
     * @param url controller IP
     * @return actual firewall rules from controller
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getFirewallRules(String url) {
        return (args) -> {
            AccessControlList[] res = client.makeRequestWithSerialization(url, new TypeReference<AccessControlList[]>(){});
            return res[0].formatRules();
        };
    }

    /**
     * 
     * @param url controller IP
     * @return deletes a specific firewall rule (ip given from input field)
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> deleteFirewallRules(String url) {
        return (args) -> {
            try {
                client.deleteFirewallRule(url, args.get("RuleId").getValue().toString());
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }

            return new SubmodelElement[] {
                new Property("All OK")
            };
        };
    }

    /**
     * 
     * @param isolate true if single host must be isolated, false otherwise
     * @param externalAccess true if external hosts must be blocked, false otherwise
     * @return
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> manageHostReachability(simulationType type) {
        return (args) -> {
            String hostIP = type.equals(simulationType.DENYEXTERNALHOST) ? 
                    "192.168.0.0/24" : args.get("HostIP").getValue() == null ? "" : args.get("HostIP").getValue().toString();

            if (hostIP.isBlank())
                throw new IllegalFieldValueException("HostIP", hostIP);

            if (type.equals(simulationType.ALLOWSINGLEHOST)) {
                simulation.manageTrafficToHost(client, hostIP, true);
            } else {
                simulation.makeFirewallDefaultAcceptance(client);
                simulation.manageTrafficToHost(client, hostIP, false);
            }
            
            return new SubmodelElement[] {
                new Property("All OK")
            };
        };
    }
}
