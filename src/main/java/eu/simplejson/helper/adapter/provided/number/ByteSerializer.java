package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class ByteSerializer extends JsonSerializer<Byte> {

    @Override
    public Byte deserialize(JsonEntity element, Field field, Json json) {
        return element.asByte();
    }

    @Override
    public JsonEntity serialize(Byte obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
