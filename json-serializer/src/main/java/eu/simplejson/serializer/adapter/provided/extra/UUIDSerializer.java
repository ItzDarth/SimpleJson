package eu.simplejson.serializer.adapter.provided.extra;

import eu.simplejson.elements.JsonString;
import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return UUID.fromString(element.asString());
    }

    @Override
    public JsonEntity serialize(UUID obj, Json json, Field field) {
        return new JsonString(obj.toString());
    }
}
