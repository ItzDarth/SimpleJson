package eu.simplejson.helper.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.exlude.ExcludeStrategy;

import java.io.Reader;
import java.util.Map;

public interface Json {

    /**
     * Registers a new {@link JsonSerializer} for a given {@link Class}
     *
     * @param typeClass the class of the object
     * @param serializer the serializer
     * @param <T> the generic
     */
    <T> void registerSerializer(Class<T> typeClass, JsonSerializer<T> serializer);

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
     * @param json the json element
     * @param typeClass the class of the object
     * @param <T> the generic
     * @return created object
     */
    <T> T fromJson(JsonEntity json, Class<T> typeClass);

    /**
     * Creates a new object of a {@link String} for a provided class
     *
     * @param json the string
     * @param typeClass the class of the object
     * @param <T> the generic
     * @return created object
     */
    <T> T fromJson(String json, Class<T> typeClass);

    /**
     * Creates a new object of a {@link Reader} for a provided class
     *
     * @param reader the reader
     * @param typeClass the class of the object
     * @param <T> the generic
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
     * The format of the json instance
     */
    JsonFormat getFormat();
}
