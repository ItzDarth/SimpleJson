package eu.simplejson.helper.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.config.JsonConfig;
import eu.simplejson.helper.exlude.ExcludeStrategy;

import java.io.File;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface Json {

    /**
     * The static current json instance (if set)
     */
    AtomicReference<Json> CURRENT_INSTANCE = new AtomicReference<>();

    /**
     * Registers a new {@link JsonSerializer} for a given {@link Class}
     *
     * @param typeClass  the class of the object
     * @param serializer the serializer
     * @param <T>        the generic
     */
    <T>
    void registerSerializer(Class<T> typeClass, JsonSerializer<T> serializer);

    /**
     * Registers a new {@link ExcludeStrategy} for this json instance
     *
     * @param strategy the strategy
     */
    void registerStrategy(ExcludeStrategy strategy);

    /**
     * Parses an Object into a {@link JsonEntity}
     *
     * @param obj the object
     * @return the json element or null if an error occured
     */
    <T> JsonEntity toJson(T obj);

    /**
     * Creates a new object of a {@link JsonEntity} for a provided class
     *
     * @param json      the json element
     * @param typeClass the class of the object
     * @param <T>       the generic
     * @return created object
     */
    <T> T fromJson(JsonEntity json, Class<T> typeClass);

    /**
     * Creates a new object of a {@link String} for a provided class
     *
     * @param json      the string
     * @param typeClass the class of the object
     * @param <T>       the generic
     * @return created object
     */
    <T> T fromJson(String json, Class<T> typeClass);

    /**
     * Creates a new object of a {@link Reader} for a provided class
     *
     * @param reader    the reader
     * @param typeClass the class of the object
     * @param <T>       the generic
     * @return created object
     */
    <T> T fromJson(Reader reader, Class<T> typeClass);

    /**
     * Gets all registered {@link JsonSerializer} for their
     * given Wrapper {@link Class}
     */
    Map<Class<?>, JsonSerializer<?>> getRegisteredSerializers();

    /**
     * Checks if this json instance
     * checks for subclasses of serializers
     */
    boolean isCheckSerializersForSubClasses();

    /**
     * Checks if arrays should be written in a single line
     */
    boolean isWriteArraysSingleLined();

    /**
     * Checks if nulled values should be serialized
     */
    boolean isSerializeNulls();

    /**
     * The format of the json instance
     */
    JsonFormat getFormat();

    /**
     * Loads the given config into memory, given the path
     * as a File object.
     *
     * <p>Implementations are recommended to cache configs
     * loaded by this method in order to keep copies
     * sane and consistent. You can expect that configs
     * loaded from the same path will be {@code ==} to each
     * other.</p>
     *
     * @param file the file from which to laod the config
     * @return the loaded config memory representation
     */
    JsonConfig loadConfig(File file);

}
