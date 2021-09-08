package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.helper.Json;
import eu.simplejson.helper.adapter.JsonSerializer;

public class IterableSerializer extends JsonSerializer<Iterable> {
    
    @Override
    public Iterable deserialize(JsonEntity element) {
        return element.asJsonArray();
    }

    @Override
    public JsonEntity serialize(Iterable obj, Json json) {
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
