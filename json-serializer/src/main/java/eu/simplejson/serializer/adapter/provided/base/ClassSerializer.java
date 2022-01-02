package eu.simplejson.serializer.adapter.provided.base;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;
import eu.simplejson.serializer.adapter.JsonSerializer;

import java.lang.reflect.Field;

public class ClassSerializer extends JsonSerializer<Class> {

    @Override
    public Class deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        try {
            return Class.forName(element.asString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonEntity serialize(Class obj, Json json, Field field) {
        return JsonEntity.valueOf(obj.getName());
    }
}
