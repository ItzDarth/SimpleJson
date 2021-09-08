package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

import java.util.UUID;

public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element) {
        return UUID.fromString(element.asString());
    }

    @Override
    public JsonEntity serialize(UUID obj,Json json) {
        return new JsonString(obj.toString());
    }
}
