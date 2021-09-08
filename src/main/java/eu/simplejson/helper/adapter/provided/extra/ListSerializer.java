package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.helper.Json;
import eu.simplejson.helper.adapter.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class ListSerializer extends JsonSerializer<List> {
    
    @Override
    public List deserialize(JsonEntity element) {
        List list = new ArrayList();
        for (JsonEntity jsonEntity : element.asJsonArray()) {
            list.add(jsonEntity.asObject());
        }
        return list;
    }

    @Override
    public JsonEntity serialize(List obj, Json json) {
        JsonArray jsonArray = new JsonArray();

        for (Object o : obj) {

            JsonEntity entity = JsonEntity.valueOf(o);
            if (entity == null) {
                jsonArray.add(json.toJson(o));
            } else {
                jsonArray.add(entity);
            }
        }
        return jsonArray;
    }
}
