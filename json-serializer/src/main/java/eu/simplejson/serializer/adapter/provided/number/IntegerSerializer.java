package eu.simplejson.serializer.adapter.provided.number;

import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;

public class IntegerSerializer extends JsonSerializer<Integer> {

    @Override
    public Integer deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {

        return element.asInt();
    }

    @Override
    public JsonEntity serialize(Integer obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
