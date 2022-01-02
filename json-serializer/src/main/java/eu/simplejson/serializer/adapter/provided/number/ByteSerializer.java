package eu.simplejson.serializer.adapter.provided.number;

import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;

public class ByteSerializer extends JsonSerializer<Byte> {

    @Override
    public Byte deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asByte();
    }

    @Override
    public JsonEntity serialize(Byte obj, Json json, Field field) {
        return JsonEntity.valueOf(obj);
    }
}
