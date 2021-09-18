package eu.simplejson.helper;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.adapter.provided.base.BooleanSerializer;
import eu.simplejson.helper.adapter.provided.base.EnumSerializer;
import eu.simplejson.helper.adapter.provided.base.StringSerializer;
import eu.simplejson.helper.adapter.provided.extra.*;
import eu.simplejson.helper.adapter.provided.number.*;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Json {

    @Getter
    private static Json instance = new Json().serializeNulls().checkSerializersForSubClasses().setFormat(JsonFormat.FORMATTED);

    /**
     * All registered serializers
     */
    private final Map<Class<?>, JsonSerializer<?>> registeredSerializers;

    /**
     * The format for printing
     */
    private JsonFormat format;

    /**
     * If nulls should be serialized
     */
    private boolean serializeNulls;

    /**
     * If an object that no serializer was found for
     * should check for all sub classes if a serializer exists
     */
    private boolean checkSerializersForSubClasses;

    public Json() {
        instance = this;
        this.registeredSerializers = new ConcurrentHashMap<>();
        this.format = JsonFormat.RAW;
        this.checkSerializersForSubClasses = false;

        //Registering default serializers
        this.registerSerializer(String.class, new StringSerializer());
        this.registerSerializer(Boolean.class, new BooleanSerializer());

        //Numbers
        this.registerSerializer(Number.class, new NumberSerializer());
        this.registerSerializer(Integer.class, new IntegerSerializer());
        this.registerSerializer(Double.class, new DoubleSerializer());
        this.registerSerializer(Short.class, new ShortSerializer());
        this.registerSerializer(Float.class, new FloatSerializer());
        this.registerSerializer(Byte.class, new ByteSerializer());
        this.registerSerializer(Long.class, new LongSerializer());

        //Extra values
        this.registerSerializer(UUID.class, new UUIDSerializer());
        this.registerSerializer(Enum.class, new EnumSerializer());
        this.registerSerializer(JsonLiteral.class, new LiteralSerializer());

        //Storage types
        this.registerSerializer(Iterable.class, new IterableSerializer());
        this.registerSerializer(List.class, new ListSerializer());
        this.registerSerializer(Map.class, new MapSerializer());
    }

    /**
     * Registers a new {@link JsonSerializer} for a given {@link Class}
     *
     * @param typeClass the class of the object
     * @param serializer the serializer
     * @param <T> the generic
     * @return current json
     */
    public <T> Json registerSerializer(Class<T> typeClass, JsonSerializer<T> serializer) {
        this.registeredSerializers.put(typeClass, serializer);
        return this;
    }

    /**
     * Sets the {@link JsonFormat} of this instance
     *
     * @param format the format
     * @return current json
     */
    public Json setFormat(JsonFormat format) {
        this.format = format;
        return this;
    }

    /**
     * Enables serializing nulls
     *
     * @return current json
     */
    public Json serializeNulls() {
        this.serializeNulls = true;
        return this;
    }

    /**
     * Enables checking for sub-class serializers
     *
     * @return current json
     */
    public Json checkSerializersForSubClasses() {
        this.checkSerializersForSubClasses = true;
        return this;
    }


    /**
     * Parses an Object into a {@link JsonEntity}
     *
     * @param obj the object
     * @return the json element or null if an error occured
     */
    public <T> JsonEntity toJson(T obj) {
        try {
            if (obj == null) {
                return null;
            }
            JsonEntity jsonEntity;
            if (this.registeredSerializers.containsKey(obj.getClass())) {
                JsonSerializer<T> jsonSerializer = (JsonSerializer<T>) this.registeredSerializers.get(obj.getClass());
                jsonEntity = jsonSerializer.serialize(obj, this);
            } else {
                if (obj instanceof Iterable<?>) {
                    JsonArray jsonArray = new JsonArray();
                    Iterable<?> iterable = (Iterable<?>)obj;

                    for (Object o : iterable) {
                        JsonEntity entity = JsonEntity.valueOf(o);
                        if (entity == null) {
                            jsonArray.add(toJson(o));
                        } else {
                            jsonArray.add(entity);
                        }
                    }

                    jsonEntity = jsonArray;
                } else {
                    JsonObject jsonObject = new JsonObject();
                    for (Field declaredField : obj.getClass().getDeclaredFields()) {
                        declaredField.setAccessible(true);

                        JsonSerializer adapterOrNull = JsonUtils.getSerializerOrNull(this, declaredField.getType());
                        if (adapterOrNull != null) {
                            jsonObject.add(declaredField.getName(), adapterOrNull.serialize(declaredField.get(obj), this));
                        } else {
                            Object o = declaredField.get(obj);
                            if (o == null) {
                                jsonObject.add(declaredField.getName(), JsonLiteral.NULL);
                            } else {

                                JsonEntity entity = JsonEntity.valueOf(o);

                                if (entity == null) {
                                    jsonObject.add(declaredField.getName(), toJson(o));
                                } else {
                                    jsonObject.add(declaredField.getName(), entity);
                                }

                            }
                        }

                    }
                    jsonEntity = jsonObject;
                }
            }
            jsonEntity.setFormat(this.format);
            return jsonEntity;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new object of a {@link JsonEntity} for a provided class
     *
     * @param json the json element
     * @param typeClass the class of the object
     * @param <T> the generic
     * @return created object
     */
    @SneakyThrows
    public <T> T fromJson(JsonEntity json, Class<T> typeClass) {
        //Trying to get from serializer
        T object = JsonUtils.getSerializedOrNull(this, json, typeClass);

        //No serializer... trying with empty object
        if (object == null) {
            object = JsonUtils.createEmptyObject(typeClass);
        }

        //Object still null returning null
        if (object == null) {
            return null;
        }

        if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;

            for (String key : jsonObject.keySet()) {
                Field declaredField = object.getClass().getDeclaredField(key);
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();

                JsonEntity jsonEntity = jsonObject.get(key);
                Object value = JsonUtils.getSerializedOrNull(this, jsonEntity, type);


                if (type.isPrimitive() && value == null) {
                    Class<?> wrapperClassForPrimitive = JsonUtils.getWrapperClassForPrimitive(type);
                    value = JsonUtils.getSerializedOrNull(this, jsonEntity, wrapperClassForPrimitive);
                    if (value == null) {
                        continue;
                    }
                } else if (value == null) {
                    value = fromJson(jsonEntity, declaredField.getType());
                }

                declaredField.set(object, value);
            }
        }
        return object;
    }
}
