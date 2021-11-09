package eu.simplejson.helper.adapter.provided.extra;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.annotation.SerializedField;
import eu.simplejson.helper.annotation.WrapperClass;
import eu.simplejson.helper.json.Json;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer extends JsonSerializer<Map> {
    
    @Override @SneakyThrows
    public Map deserialize(JsonEntity element, Field field, Json json) {
        Map map = new HashMap();

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.asJsonObject();
            Class<?> typeClass = null;
            if (field == null) {
                for (String key : jsonObject.keySet()) {
                    JsonEntity entity = jsonObject.get(key);

                    if (entity.isJsonObject()) {
                        JsonObject object = (JsonObject)entity;
                        if (object.has("@type")) {
                            typeClass = Class.forName(object.get("@type").asString());
                        }
                    }
               }
            } else {
                if (field.getType().isInterface() && field.getAnnotation(SerializedField.class) != null) {
                    SerializedField annotation = field.getAnnotation(SerializedField.class);
                    for (WrapperClass wrapperClass : annotation.wrapperClasses()) {
                        if (wrapperClass.interfaceClass().equals(field.getType())) {
                            typeClass = wrapperClass.value();
                        }
                    }
                }
                if (typeClass == null) {
                    typeClass = field.getType();
                }
            }

            for (String key : jsonObject.keySet()) {
                if (typeClass == null) {
                    map.put(key, jsonObject.get(key).asObject());
                } else {
                    Object o = jsonObject.get(key).asObject();

                    map.put(key, o == null ? json.fromJson(jsonObject.get(key), typeClass) : o);
                }
            }
        } else {
            throw new UnsupportedOperationException("[Json/Serializer] MapSerializer.class can not deserialize JsonEntity of Type " + element.jsonType() + "! It has to be a JsonObject!");
        }

        return map;
    }

    @Override
    public JsonEntity serialize(Map obj, Json json, Field field) {

        JsonObject jsonObject = new JsonObject();

        if (obj != null && !obj.isEmpty()) {
            obj.forEach((key, value) -> {
                JsonEntity entity = JsonEntity.valueOf(value);

                if (entity == null) {
                    entity = json.toJson(value);

                }
                if (field == null) {
                    if (entity.isJsonObject()) {
                        ((JsonObject)entity).addProperty("@type", value.getClass().getName());
                    }
                }
                jsonObject.addProperty(key.toString(), entity);
            });

        }
        return jsonObject;
    }
}
