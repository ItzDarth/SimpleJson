package eu.simplejson.helper.adapter.provided.base;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class StringSerializer extends JsonSerializer<String> {

    @Override
    public String deserialize(JsonEntity element) {
        if (element.isNull()) {
            return null;
        }
        return element.asString();
    }

    @Override
    public JsonEntity serialize(String obj,Json json) {
        return obj == null ? JsonLiteral.NULL : new JsonString(obj);
    }
}
