package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.helper.Json;
import eu.simplejson.helper.adapter.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class MapSerializer extends JsonSerializer<Map> {
    
    @Override
    public Map deserialize(JsonEntity element) {
        Map map = new HashMap();

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.asJsonObject();
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getObject(key));
            }
        } else {
            throw new UnsupportedOperationException("[Json/Serializer] MapSerializer.class can not deserialize JsonEntity of Type " + element.getType() + "! It has to be a JsonObject!");
        }

        return map;
    }

    @Override
    public JsonEntity serialize(Map obj, Json json) {

        JsonObject jsonObject = new JsonObject();

        if (obj != null) {
            obj.forEach((key, value) -> {
                jsonObject.addSerialized(key.toString(), value);
            });

        }
        return jsonObject;
    }
}
