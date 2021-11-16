package eu.simplejson.helper.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonNumber;
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
import eu.simplejson.helper.config.JsonConfig;
import eu.simplejson.helper.config.SimpleJsonConfig;
import eu.simplejson.helper.config.SimpleJsonSection;
import eu.simplejson.helper.exlude.ExcludeStrategy;
import eu.simplejson.helper.parsers.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
class SimpleJsonInstance implements Json {

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

    /**
     * If primitive arrays should be written like this : [1, 2, 3, 4, 5, 6]
     */
    private final boolean writeArraysSingleLined;

    SimpleJsonInstance(JsonFormat format, boolean serializeNulls, int serializeSameFieldInstance, boolean checkSerializersForSubClasses, boolean writeArraysSingleLined) {
        this.format = format;
        this.serializeNulls = serializeNulls;
        this.serializeSameFieldInstance = serializeSameFieldInstance;
        this.checkSerializersForSubClasses = checkSerializersForSubClasses;
        this.writeArraysSingleLined = writeArraysSingleLined;

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
        //this.registerSerializer(Iterable.class, new IterableSerializer());
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
        if (JsonEntity.valueOf(obj) != null) {
            return JsonEntity.valueOf(obj);
        }
        try {
            if (obj == null) {
                return JsonLiteral.NULL;
            }
            JsonEntity jsonEntity;
            boolean contains = this.registeredSerializers.containsKey(obj.getClass());
            Class<?> typeClass = obj.getClass();
            if (checkSerializersForSubClasses) {
                for (Class<?> aClass : JsonHelper.loadAllSubClasses(obj.getClass())) {
                    if (this.registeredSerializers.containsKey(aClass)) {
                        contains = true;
                        typeClass = aClass;
                        break;
                    }
                }
            }
            if (contains) {
                JsonSerializer<T> jsonSerializer = (JsonSerializer<T>) this.registeredSerializers.get(typeClass);
                jsonEntity = jsonSerializer.serialize(obj, this, null);
            } else {
                if (obj instanceof Iterable<?> ) {
                    JsonArray jsonArray = new JsonArray();
                    Iterable<?> iterable = (Iterable<?>) obj;

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
                                        type = wrapperClass.value();
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

    /**
     * Init safety static factory method; instance of this
     * class is published when creating new config sections
     * via the load method in {@link SimpleJsonSection}
     *
     * @param file the file to config
     * @return the loaded config
     */
    @Override
    public JsonConfig loadConfig(File file) {
        return SimpleJsonConfig.CACHE.computeIfAbsent(file.toPath(), k -> {
            SimpleJsonConfig config = new SimpleJsonConfig(file.toPath());
            config.load();

            return config;
        });
    }

    @Override
    @SneakyThrows
    public <T> T fromJson(JsonEntity json, Class<T> typeClass) {
        if (typeClass.isAnnotationPresent(WrapperClass.class)) {
            WrapperClass annotation = typeClass.getAnnotation(WrapperClass.class);
            typeClass = (Class<T>) annotation.value();
        }
        //Trying to get from serializer
        T object = JsonHelper.getSerializedOrNull(this, json, typeClass, null);

        //No serializer... trying with empty object
        if (object == null) {
            object = JsonHelper.createEmptyObject(typeClass);
        } else {
            return object;
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
                            type = wrapperClass.value();
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
        } else if (json instanceof JsonString) {
            object = (T) json.asString();
        } else if (json instanceof JsonArray) {
            List<T> list = new ArrayList<>();
            for (JsonEntity entity : ((JsonArray) json)) {
                Object obj = entity.asObject();

                if (obj == null) {
                    continue;
                }

                list.add((T) obj);
                System.out.println(entity);
                //TODO
            }
            object = (T) list;
        } else if (json instanceof JsonNumber) {
            JsonNumber jsonNumber = (JsonNumber) json;
            object = (T) jsonNumber.asObject();
        } else {
            System.out.println("Json could not deserialize Entity of type " + json.jsonType() + "!");
        }
        return object;
    }
}
