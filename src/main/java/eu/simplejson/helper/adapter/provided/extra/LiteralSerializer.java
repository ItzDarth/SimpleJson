package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class LiteralSerializer extends JsonSerializer<JsonLiteral> {


    @Override
    public JsonLiteral deserialize(JsonEntity element) {

        if (element.asString().equals("null") || element.equals(JsonLiteral.NULL)) {
            return (JsonLiteral) JsonLiteral.NULL;
        }
        if (element.asString().equalsIgnoreCase("true") || element.equals(JsonLiteral.TRUE)) {
            return (JsonLiteral) JsonLiteral.TRUE;
        }
        if (element.asString().equalsIgnoreCase("false") || element.equals(JsonLiteral.FALSE)) {
            return (JsonLiteral) JsonLiteral.FALSE;
        }
        return null;
    }

    @Override
    public JsonEntity serialize(JsonLiteral obj,Json json) {
        return new JsonString(obj.toString());
    }
}
