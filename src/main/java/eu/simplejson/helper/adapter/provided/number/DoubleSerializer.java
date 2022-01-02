package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

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
