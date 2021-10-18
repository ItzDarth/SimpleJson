package eu.simplejson.helper.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonString;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.JsonHelper;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.adapter.provided.base.BooleanSerializer;
import eu.simplejson.helper.adapter.provided.base.ClassSerializer;
import eu.simplejson.helper.adapter.provided.base.EnumSerializer;
import eu.simplejson.helper.adapter.provided.base.StringSerializer;
import eu.simplejson.helper.adapter.provided.extra.*;
import eu.simplejson.helper.adapter.provided.number.*;
import eu.simplejson.helper.annotation.SerializedField;
import eu.simplejson.helper.annotation.SerializedObject;
import eu.simplejson.helper.annotation.WrapperClass;
import eu.simplejson.helper.exlude.ExcludeStrategy;
import eu.simplejson.helper.parsers.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonBuilder {

    /**
     * Creates a new {@link JsonBuilder} instance
     */
    public static JsonBuilder newBuilder() {
        return new JsonBuilder();
    }

    /**
     * The format for printing
     */
    private JsonFormat format;

    /**
     * If nulls should be serialized
     */
    private boolean serializeNulls;

    /**
     * The amount of times a field of an object
     * will be serialized if its the same type as the class
     * (to prevent StackOverFlow)
     */
    private int serializeSameFieldInstance;

    /**
     * If an object that no serializer was found for
     * should check for all subclasses if a serializer exists
     */
    private boolean checkSerializersForSubClasses;

    private JsonBuilder() {
        this.format = JsonFormat.RAW;
        this.serializeNulls = false;
        this.serializeSameFieldInstance = 10;
        this.checkSerializersForSubClasses = true;
    }

    /**
     * Uses the recommended settings
     *
     * @return current json
     */
    public JsonBuilder recommendedSettings() {
        this.format = JsonFormat.FORMATTED;
        this.serializeNulls = true;
        this.serializeSameFieldInstance = 10;
        this.checkSerializersForSubClasses = true;
        return this;
    }

    /**
     * Sets the amount of times a field of an object
     * will be serialized if it's the same type as the class
     * (to prevent StackOverFlow)
     * @param times the amount
     * @return current json
     */
    public JsonBuilder serializeSameFieldInstance(int times) {
        this.serializeSameFieldInstance = times;
        return this;
    }

    /**
     * Sets the {@link JsonFormat} of this instance
     *
     * @param format the format
     * @return current json
     */
    public JsonBuilder setFormat(JsonFormat format) {
        this.format = format;
        return this;
    }

    /**
     * Enables serializing nulls
     *
     * @return current json
     */
    public JsonBuilder serializeNulls() {
        this.serializeNulls = true;
        return this;
    }

    /**
     * Enables checking for sub-class serializers
     *
     * @return current json
     */
    public JsonBuilder checkSerializersForSubClasses() {
        this.checkSerializersForSubClasses = true;
        return this;
    }


    /**
     * Builds this instance
     */
    public Json build() {
        return new JsonImpl(format, serializeNulls, serializeSameFieldInstance, checkSerializersForSubClasses);
    }

    @Getter
    private static class JsonImpl implements Json {

        /**
         * All registered serializers
         */
        private final Map<Class<?>, JsonSerializer<?>> registeredSerializers;

        /**
         * All registered strategies
         */
        private final List<ExcludeStrategy> excludeStrategies;

        /**
         * The format for printing
         */
        private final JsonFormat format;

        /**
         * If nulls should be serialized
         */
        private final boolean serializeNulls;

        /**
         * The amount of times a field of an object
         * will be serialized if its the same type as the class
         * (to prevent StackOverFlow)
         */
        private final int serializeSameFieldInstance;

        /**
         * If an object that no serializer was found for
         * should check for all subclasses if a serializer exists
         */
        private final boolean checkSerializersForSubClasses;

        private JsonImpl(JsonFormat format, boolean serializeNulls, int serializeSameFieldInstance, boolean checkSerializersForSubClasses) {
            this.format = format;
            this.serializeNulls = serializeNulls;
            this.serializeSameFieldInstance = serializeSameFieldInstance;
            this.checkSerializersForSubClasses = checkSerializersForSubClasses;

            this.registeredSerializers = new ConcurrentHashMap<>();
            this.excludeStrategies = new ArrayList<>();


            //Registering default serializers
            this.registerSerializer(String.class, new StringSerializer());
            this.registerSerializer(Boolean.class, new BooleanSerializer());
            this.registerSerializer(Class.class, new ClassSerializer());

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

        @Override
        public <T> void registerSerializer(Class<T> typeClass, JsonSerializer<T> serializer) {
            this.registeredSerializers.put(typeClass, serializer);
        }

        @Override
        public void registerStrategy(ExcludeStrategy strategy) {
            this.excludeStrategies.add(strategy);
        }

        private <T> JsonEntity toJson(T obj, int currentTry, int maxTry) {
            try {
                if (obj == null) {
                    return JsonLiteral.NULL;
                }
                JsonEntity jsonEntity;
                if (this.registeredSerializers.containsKey(obj.getClass())) {
                    JsonSerializer<T> jsonSerializer = (JsonSerializer<T>) this.registeredSerializers.get(obj.getClass());
                    jsonEntity = jsonSerializer.serialize(obj, this, null);
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
                        List<ExcludeStrategy> excludeStrategies = this.excludeStrategies;
                        SerializedObject annotation = obj.getClass().getAnnotation(SerializedObject.class);
                        Class<?>[] excludeClasses = new Class[0];

                        if (annotation != null) {
                            excludeClasses = annotation.excludeClasses();
                            maxTry = annotation.serializeSameFieldInstance();

                            Class<? extends ExcludeStrategy> strategy = annotation.strategy();

                            if (strategy != ExcludeStrategy.class) {
                                excludeStrategies.clear();
                                excludeStrategies.add(strategy.newInstance());
                            }

                        }

                        if (obj.getClass().getDeclaredFields().length == 0) {
                            jsonEntity = new JsonString(obj.getClass().getName());
                        } else {
                            for (Field declaredField : obj.getClass().getDeclaredFields()) {
                                declaredField.setAccessible(true);

                                String name = declaredField.getName();
                                Class<?> type = declaredField.getType();
                                Object fieldObject = declaredField.get(obj);
                                boolean ignore = false;
                                SerializedField serializedField = declaredField.getAnnotation(SerializedField.class);

                                if (serializedField != null) {
                                    if (!serializedField.name().trim().isEmpty()) {
                                        name = serializedField.name();
                                    }

                                    for (WrapperClass wrapperClass : serializedField.wrapperClasses()) {
                                        if (type.equals(wrapperClass.interfaceClass())) {
                                            type = wrapperClass.wrapperClass();
                                        }
                                    }

                                    ignore = serializedField.ignore();
                                }

                                if (Arrays.asList(excludeClasses).contains(type) || ignore) {
                                    continue;
                                }

                                boolean cont = false;

                                for (ExcludeStrategy excludeStrategy : excludeStrategies) {
                                    if (excludeStrategy.shouldSkipField(declaredField) || excludeStrategy.shouldSkipClass(type)) {
                                        cont = true;
                                    }
                                }

                                if (cont) {
                                    continue;
                                }

                                JsonSerializer adapterOrNull = JsonHelper.getSerializerOrNull(this, type);
                                if (adapterOrNull != null) {
                                    jsonObject.addProperty(name, adapterOrNull.serialize(fieldObject, this, declaredField));
                                } else {
                                    if (fieldObject == null) {
                                        jsonObject.addProperty(name, JsonLiteral.NULL);
                                    } else {

                                        JsonEntity entity = JsonEntity.valueOf(fieldObject);

                                        if (entity == null) {
                                            if (type.equals(obj.getClass())) {
                                                if (currentTry != -1 && (currentTry > this.serializeSameFieldInstance)) {
                                                    jsonObject.addProperty(name, JsonLiteral.NULL);
                                                } else {
                                                    jsonObject.addProperty(name, this.toJson(fieldObject, (currentTry + 1), maxTry));
                                                }
                                            } else {
                                                jsonObject.addProperty(name, this.toJson(fieldObject, (currentTry + 1), maxTry));
                                            }
                                        } else {
                                            jsonObject.addProperty(name, entity);
                                        }

                                    }
                                }

                            }
                            jsonEntity = jsonObject;
                        }
                    }
                }
                jsonEntity.setFormat(this.format);
                return jsonEntity;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public <T> JsonEntity toJson(T obj) {
            return this.toJson(obj, 0, this.serializeSameFieldInstance);
        }

        @Override
        public <T> T fromJson(String json, Class<T> typeClass) {
            return fromJson(new JsonParser(format).parse(json), typeClass);
        }

        @Override
        public <T> T fromJson(Reader reader, Class<T> typeClass) {
            return fromJson(new JsonParser(format).parse(reader), typeClass);
        }

        @Override
        @SneakyThrows
        public <T> T fromJson(JsonEntity json, Class<T> typeClass) {
            //Trying to get from serializer
            T object = JsonHelper.getSerializedOrNull(this, json, typeClass, null);

            //No serializer... trying with empty object
            if (object == null) {
                object = JsonHelper.createEmptyObject(typeClass);
            }

            //Object still null returning null
            if (object == null) {
                return null;
            }
            List<ExcludeStrategy> excludeStrategies = this.excludeStrategies;
            SerializedObject annotation = object.getClass().getAnnotation(SerializedObject.class);
            Class<?>[] excludeClasses = new Class[0];

            if (annotation != null) {
                excludeClasses = annotation.excludeClasses();
                Class<? extends ExcludeStrategy> strategy = annotation.strategy();
                if (strategy != ExcludeStrategy.class) {
                    excludeStrategies.clear();
                    excludeStrategies.add(strategy.newInstance());
                }
            }

            if (json instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) json;

                for (String key : jsonObject.keySet()) {
                    Field declaredField;
                    try {
                        declaredField = object.getClass().getDeclaredField(key);
                    } catch (NoSuchFieldException e) {
                        declaredField = Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(SerializedField.class) != null && field.getAnnotation(SerializedField.class).name().equalsIgnoreCase(key)).findFirst().orElse(null);
                    }

                    if (declaredField == null) {
                        continue;
                    }

                    declaredField.setAccessible(true);

                    String name = declaredField.getName();
                    Class<?> type = declaredField.getType();
                    boolean ignore = false;
                    SerializedField serializedField = declaredField.getAnnotation(SerializedField.class);

                    if (serializedField != null) {
                        if (!serializedField.name().trim().isEmpty()) {
                            name = serializedField.name();
                        }

                        for (WrapperClass wrapperClass : serializedField.wrapperClasses()) {
                            if (type.equals(wrapperClass.interfaceClass())) {
                                type = wrapperClass.wrapperClass();
                            }
                        }

                        ignore = serializedField.ignore();
                    }

                    if (Arrays.asList(excludeClasses).contains(type) || ignore) {
                        continue;
                    }

                    boolean cont = false;
                    for (ExcludeStrategy excludeStrategy : excludeStrategies) {
                        if (excludeStrategy.shouldSkipField(declaredField) || excludeStrategy.shouldSkipClass(type)) {
                            cont = true;
                        }
                    }
                    if (cont) {
                        continue;
                    }

                    JsonEntity jsonEntity = jsonObject.get(name);
                    Object value = JsonHelper.getSerializedOrNull(this, jsonEntity, type, declaredField);

                    if (type.isPrimitive() && value == null) {
                        Class<?> wrapperClassForPrimitive = JsonHelper.getWrapperClassForPrimitive(type);
                        value = JsonHelper.getSerializedOrNull(this, jsonEntity, wrapperClassForPrimitive, declaredField);
                        if (value == null) {
                            continue;
                        }
                    } else if (value == null) {

                        value = fromJson(jsonEntity, type);
                    }

                    declaredField.set(object, value);
                }
            } else if (json instanceof JsonLiteral) {
                JsonLiteral jsonLiteral = (JsonLiteral) json;
                if (jsonLiteral == JsonLiteral.TRUE && (typeClass == Boolean.class || typeClass == boolean.class)) {
                    object = (T) Boolean.TRUE;
                } else if (jsonLiteral == JsonLiteral.FALSE && (typeClass == Boolean.class || typeClass == boolean.class)) {
                    object = (T) Boolean.FALSE;
                } else if (jsonLiteral == JsonLiteral.NULL) {
                    object = null;
                }
            } else if (json instanceof JsonString && typeClass == String.class) {
                object = (T) json.asString();
            } else if (json instanceof JsonArray && Iterable.class.isAssignableFrom(typeClass)) {
                List<T> list = new ArrayList<>();
                for (JsonEntity entity : ((JsonArray)json)) {
                    Object obj = entity.asObject();

                    if (obj == null) {
                        continue;
                    }

                    list.add((T) obj);
                    System.out.println(entity);
                    //TODO
                }
                object = (T) list;
            }
            return object;
        }
    }
}
