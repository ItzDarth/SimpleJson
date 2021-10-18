package eu.simplejson.helper.adapter.provided.base;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class ClassSerializer extends JsonSerializer<Class> {

    @Override
    public Class deserialize(JsonEntity element, Field field, Json json) {
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
