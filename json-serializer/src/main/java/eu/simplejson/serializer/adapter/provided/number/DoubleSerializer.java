package eu.simplejson.serializer.adapter.provided.number;

import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;

public class DoubleSerializer extends JsonSerializer<Double> {

    @Override
    public Double deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asDouble();
    }

    @Override
    public JsonEntity serialize(Double obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
