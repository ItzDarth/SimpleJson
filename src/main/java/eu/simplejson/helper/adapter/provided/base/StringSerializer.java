package eu.simplejson.helper.adapter.provided.base;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public class StringSerializer extends JsonSerializer<String> {

    @Override
    public String deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        if (element.isNull()) {
            return null;
        }
        return element.asString();
    }

    @Override
    public JsonEntity serialize(String obj, Json json, Field field) {
        return obj == null ? JsonLiteral.NULL : new JsonString(obj);
    }
}
