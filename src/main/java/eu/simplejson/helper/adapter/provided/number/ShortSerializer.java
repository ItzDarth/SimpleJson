package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class ShortSerializer extends JsonSerializer<Short> {

    @Override
    public Short deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asShort();
    }

    @Override
    public JsonEntity serialize(Short obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
