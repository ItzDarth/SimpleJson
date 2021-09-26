package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class FloatSerializer extends JsonSerializer<Float> {

    @Override
    public Float deserialize(JsonEntity element, Field field, Json json) {
        return element.asFloat();
    }

    @Override
    public JsonEntity serialize(Float obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
