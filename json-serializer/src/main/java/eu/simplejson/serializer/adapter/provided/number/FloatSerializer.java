package eu.simplejson.serializer.adapter.provided.number;

import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;

public class FloatSerializer extends JsonSerializer<Float> {

    @Override
    public Float deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asFloat();
    }

    @Override
    public JsonEntity serialize(Float obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
