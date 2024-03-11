package p4_aas.NetworkController.Utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class Utils {
    public SubmodelElement createProperty(String idShort, Object value) {
        SubmodelElement el = new Property(idShort, value);
        el.setKind(ModelingKind.TEMPLATE);
        return el;
    }

    public List<String> jsonToList(String json) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        List<String> resultList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            resultList.add(jsonArray.get(i).toString());
        }

        return resultList;
    }
}
