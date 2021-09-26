package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class LongSerializer extends JsonSerializer<Long> {

    @Override
    public Long deserialize(JsonEntity element, Field field, Json json) {
        return element.asLong();
    }

    @Override
    public JsonEntity serialize(Long obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
