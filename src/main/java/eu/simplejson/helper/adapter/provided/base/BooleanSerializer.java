package eu.simplejson.helper.adapter.provided.base;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class BooleanSerializer extends JsonSerializer<Boolean> {

    @Override
    public Boolean deserialize(JsonEntity element) {
        return element.asBoolean();
    }

    @Override
    public JsonEntity serialize(Boolean obj,Json json) {
        return obj ? JsonLiteral.TRUE : JsonLiteral.FALSE;
    }
}
