package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class IntegerSerializer extends JsonSerializer<Integer> {

    @Override
    public Integer deserialize(JsonEntity element, Field field, Json json) {

        return element.asInt();
    }

    @Override
    public JsonEntity serialize(Integer obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
