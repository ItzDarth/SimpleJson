package eu.simplejson.serializer.adapter.provided.extra;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;

public class IterableSerializer extends JsonSerializer<Iterable> {
    
    @Override
    public Iterable deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asJsonArray();
    }

    @Override
    public JsonEntity serialize(Iterable obj, Json json, Field field) {
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
