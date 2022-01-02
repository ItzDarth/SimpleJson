package eu.simplejson.serializer.adapter.provided.extra;

import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.serializer.adapter.JsonSerializer;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.serializer.Json;
import eu.simplejson.serializer.modifier.annotation.SerializedField;
import eu.simplejson.serializer.modifier.annotation.WrapperClass;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer extends JsonSerializer<Map> {
    
    @Override @SneakyThrows
    public Map deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        Map map = new HashMap();

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.asJsonObject();
            Class<?> typeClass = null;
            Class<?> mapKeyType;
            Class<?> mapValueType = null;

            if (arguments.length == 2) {
                mapKeyType = arguments[0];
                mapValueType = arguments[1];
            } else {

                if (field == null) {
                    for (String key : jsonObject.keySet()) {
                        JsonEntity entity = jsonObject.get(key);

                        if (entity.isJsonObject()) {
                            JsonObject object = (JsonObject) entity;
                            if (object.has("@type")) {
                                mapValueType = Class.forName(object.get("@type").asString());
                            }
                        }
                    }
                } else {
                    ParameterizedType mapType = (ParameterizedType) field.getGenericType();
                    mapKeyType = (Class<?>) mapType.getActualTypeArguments()[0];
                    mapValueType = (Class<?>) mapType.getActualTypeArguments()[1];

                    if (field.getType().isInterface() && field.getAnnotation(SerializedField.class) != null) {
                        SerializedField annotation = field.getAnnotation(SerializedField.class);
                        for (WrapperClass wrapperClass : annotation.wrapperClasses()) {
                            if (wrapperClass.interfaceClass().equals(field.getType())) {
                                typeClass = wrapperClass.value();
                            } else if (wrapperClass.interfaceClass().equals(mapKeyType)) {
                                mapKeyType = wrapperClass.value();
                            } else if (wrapperClass.interfaceClass().equals(mapValueType)) {
                                mapValueType = wrapperClass.value();
                            }
                        }
                    }
                    if (typeClass == null) {
                        typeClass = field.getType();
                    }
                    if (mapValueType == null) {
                        mapValueType = typeClass;
                    }
                }
            }

            for (String key : jsonObject.keySet()) {
                if (mapValueType == null) {
                    map.put(key, jsonObject.get(key).asObject());
                } else {
                    Object o = jsonObject.get(key).asObject();

                    map.put(key, o == null ? json.fromJson(jsonObject.get(key), mapValueType) : o);
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
