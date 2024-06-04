package p4_aas.NetworkController.Serialization;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rules {

    @JsonProperty("Table")
    private String table;

    @JsonProperty("Keys")
    private List<Keys> keys;

    @JsonProperty("Action")
    private String action;

    @JsonProperty("ActionParam")
    private List<String> actionParam;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<Keys> getKeys() {
        return keys;
    }

    public void setKeys(List<Keys> keys) {
        this.keys = keys;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getActionParam() {
        return actionParam;
    }

    public void setActionParam(List<String> actionParam) {
        this.actionParam = actionParam;
    }

    @Override
    public String toString() {
        return "Rules{" +
                "table='" + table.replace("MyIngress.", "") + '\'' +
                ", keys=" + keys.toString() +
                ", action='" + action.replace("MyIngress.", "") + '\'' +
                ", actionParam=" + actionParam.toString() +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Keys {
        @JsonProperty("Value")
        private String value;

        public String getValue() {
            return value;
        }
    
        public void setValue(String value) {
            this.value = value;
        }
    
        @Override
        public String toString() {
            return "Keys {" + value + '}';
        }
    }
}
