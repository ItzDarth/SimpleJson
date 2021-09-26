package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListSerializer extends JsonSerializer<List> {
    
    @Override
    public List deserialize(JsonEntity element, Field field, Json json) {
        List list = new ArrayList();
        for (JsonEntity jsonEntity : element.asJsonArray()) {
            list.add(jsonEntity.asObject());
        }
        return list;
    }

    @Override
    public JsonEntity serialize(List obj, Json json, Field field) {
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
