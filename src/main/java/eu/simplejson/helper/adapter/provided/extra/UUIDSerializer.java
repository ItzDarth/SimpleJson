package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element, Field field, Json json) {
        return UUID.fromString(element.asString());
    }

    @Override
    public JsonEntity serialize(UUID obj, Json json, Field field) {
        return new JsonString(obj.toString());
    }
}
