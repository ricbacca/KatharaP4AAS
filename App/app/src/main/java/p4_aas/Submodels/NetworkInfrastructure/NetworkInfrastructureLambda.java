package p4_aas.Submodels.NetworkInfrastructure;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import p4_aas.NetworkController.NetworkController;
import p4_aas.NetworkController.Serialization.RuleDescribers;
import p4_aas.NetworkController.Utils.ApiEnum;
import p4_aas.Submodels.NetworkInfrastructure.YamlParser.Rules;
import p4_aas.Submodels.NetworkInfrastructure.YamlParser.Switches;
import p4_aas.Submodels.NetworkInfrastructure.YamlParser.YamlParser;

public class NetworkInfrastructureLambda {
    private NetworkController client;
    private YamlParser parser;
    private List<Switches> rulesForSwitchAndProgram;

    public NetworkInfrastructureLambda(NetworkController client) {
        this.client = client;
        this.parser = new YamlParser();
        this.rulesForSwitchAndProgram = this.parser.parseYaml();
    }

    /**
     * Used into getting current program of specified Switch.
     * @return lambda function for Submodel Operations
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> getRequest() {
        return (args) -> {
            String result = "null";
            Integer Switch = ((BigInteger) args.get("Switch").getValue()).intValue();

            if (Switch == 1)
                result = this.client.getRequest(ApiEnum.CURRENTPROGRAM_SW1.url);
            else if (Switch == 2)
                result = this.client.getRequest(ApiEnum.CURRENTPROGRAM_SW2.url);

            
            return new SubmodelElement[]{
                new Property(result)
            };
        };
    }

    /**
     * Used into Operation to change executed program in specified P4 Switch.
     * @return lambda function.
     */
    public Function<Map<String, SubmodelElement>, SubmodelElement[]> changeProgram() {
       return (args) -> {
            this.changeSwitchProgram(this.getSwitchID(args), this.getProgram(args));

            Optional<List<Rules>> filteredRules = Optional.empty();
            try {
                filteredRules = rulesForSwitchAndProgram
                .stream()
                .filter(el -> el.getID().equals("s" + this.getSwitchID(args)))
                .map(el -> el.getRules(this.getProgram(args))).findAny(); 
            } catch (NullPointerException e) {
                throw new NullPointerException("No rules for switch and program given combination !");
            }

            if (filteredRules.isPresent())
                postRules(this.getSwitchID(args), this.getProgram(args), this.getHost(args), filteredRules.get());
            return new SubmodelElement[]{};
        };
    }

    public void postRules(int switchID, String program, String host, List<Rules> filteredRules) {
        List<RuleDescribers> ruleDescribers = client.getRuleDescribers(
            switchID == 1 ? ApiEnum.RULEDESCRIBER_SW1.url : ApiEnum.RULEDESCRIBER_SW2.url);

        // Se viene fornito l'host e quindi è un programma che c'entra...
        // 1. Tolgo le regole di inoltro per l'host fornito
        // 2. Sostituisco nelle regole "modello" di drop, il valore "X.X.X.X" con l'host fornito (vedi metodo in Keys)
        //    Queste regole di drop sono 2 (2 per ogni switch), una che droppa in base al SRC_IP e una con DST_IP
        if (host != null && program.equals("blockSingleHost")) {
            filteredRules.removeIf(el -> ((el.getTable().equals("MyIngress.ipv4_exact") && (el.getKeys().equals(host)))));
            filteredRules.forEach(el -> el.setKeyValue(host));
        }

        filteredRules.forEach(rule -> {
            // Qui trovo il Rule Describer corrispondente alla Rule attuale
            // La lista di Rule è filtrata per Switch e Program scelti dall'utente
            Optional<RuleDescribers> ruleDescriber = ruleDescribers.stream().
                filter(rd -> ((rd.getTableName().equals(rule.getTable())) && 
                                (rd.getActionName().equals(rule.getAction())))).findFirst();
            
            // Se c'è un Rule Describer corrispondente:
            //    1. Mi creo l'URL usando TableId e ActionID (vedi metodo nella classe RuleDescribers).
            //    2. Ottendo i valori delle Keys per la regola da inserire, c'è sempre e solo UNA chiave.
            //       Il Nome lo prendo dal Rule Describer, il valore dalla Rule.
            //    3. Ottengo i valori dei ActionParams. Due tipi di actionParams: mac_address e port (vedi metodo nella classe Rules).
            //       Come prima il nome lo prendo dal Rule Describer,
            //       e il valore (dopo aver capito se è un mac_address o port) lo prendo dalla Rule.
            //    4. Unisco le due Map dei valori di input e invio tutto al Controller.
            if (ruleDescriber.isPresent()) {
                String URL = ApiEnum.ADDRULE_SW1.url + ruleDescriber.get().getUrlForRule();
                Map<String, String> inputValues = ruleDescriber.get().
                    getKeys().
                    stream().collect(Collectors.toMap(
                        key -> (String) (key.getName() + ":" + key.getMatchType()),
                        key -> (String) (rule.getKeys())
                    ));
                Map<String, String> actionParamsInputValues = ruleDescriber.
                    get().
                    getActionParams().
                    stream().collect(Collectors.toMap(
                        (actionParam) -> (String) (actionParam.getName() + ":" + actionParam.getPattern()),
                        (actionParam) -> (String) (rule.getValueForMatchedActionParam(actionParam.getPattern())))
                    );

                inputValues.putAll(actionParamsInputValues);
                client.postRule(URL, inputValues);                
            }
        });
    }

    private void changeSwitchProgram(int switchId, String program) {
        String URL = (switchId == 1 ?
            ApiEnum.CHANGEPROGRAM_SW1.url :
            ApiEnum.CHANGEPROGRAM_SW2.url) + program;
        client.getRequest(URL);
        
    }

    private int getSwitchID(Map<String, SubmodelElement> args) {
        return ((BigInteger) args.get("Switch").getValue()).intValue();
    }

    private String getProgram(Map<String, SubmodelElement> args) {
        return (String) args.get("Program").getValue();
    }

    private String getHost(Map<String, SubmodelElement> args) {
        String host = null;
        if (args.get("Host").getValue() != null)
            host = (String) args.get("Host").getValue();
        return host;
    }
}
