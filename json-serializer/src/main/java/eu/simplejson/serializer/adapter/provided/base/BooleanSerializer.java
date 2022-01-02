package eu.simplejson.serializer.adapter.provided.base;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.serializer.adapter.JsonSerializer;

import java.lang.reflect.Field;

public class BooleanSerializer extends JsonSerializer<Boolean> {


    @Override
    public Boolean deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return element.asBoolean();
    }

    @Override
    public JsonEntity serialize(Boolean obj, Json json, Field field) {
        return obj ? JsonLiteral.TRUE : JsonLiteral.FALSE;
    }
}
