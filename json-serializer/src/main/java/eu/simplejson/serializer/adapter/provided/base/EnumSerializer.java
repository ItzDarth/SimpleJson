package eu.simplejson.serializer.adapter.provided.base;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;
import eu.simplejson.elements.JsonString;
import eu.simplejson.serializer.adapter.JsonSerializer;

import java.lang.reflect.Field;

public class EnumSerializer extends JsonSerializer<Enum> {

    @Override
    public Enum deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        try {
            Class enumClass = field.getType();
            return Enum.valueOf(enumClass, element.asString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonEntity serialize(Enum obj, Json json, Field field) {
        return new JsonString(obj.name());
    }
}
