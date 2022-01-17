package eu.simplejson.serializer.builder;

import eu.simplejson.api.SimpleProvider;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.serializer.Json;
import eu.simplejson.serializer.adapter.JsonSerializer;
import eu.simplejson.serializer.modules.SimpleParserModule;

import java.util.HashMap;
import java.util.Map;

public class JsonBuilder {

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
    private int innerClassSerialization;

    /**
     * If an object that no serializer was found for
     * should check for all subclasses if a serializer exists
     */
    private boolean checkSerializersForSubClasses;

    /**
     * If primitive arrays should be written like this : [1, 2, 3, 4, 5, 6]
     */
    private boolean writeArraysSingleLined;

    /**
     * Extra serializers
     */
    private final Map<Class<?>, JsonSerializer<?>> serializers;

    public JsonBuilder() {
        this.format = JsonFormat.RAW;
        this.serializeNulls = true;
        this.writeArraysSingleLined = false;
        this.innerClassSerialization = 2;
        this.checkSerializersForSubClasses = true;
        this.serializers = new HashMap<>();

        //Setting all providers
        SimpleProvider.getInstance().setParserModule(new SimpleParserModule());
    }

    /**
     * Sets the amount of times a field of an object
     * will be serialized if it's the same type as the class
     * (to prevent StackOverFlow)
     *
     * @param times the amount
     * @return current json
     */
    public JsonBuilder innerClassSerialization(int times) {
        this.innerClassSerialization = times;
        return this;
    }

    /**
     * Sets {@link JsonBuilder#writeArraysSingleLined} to true
     *
     * @return current builder
     */
    public JsonBuilder writeArraysSingleLined(boolean state) {
        this.writeArraysSingleLined = state;
        return this;
    }

    /**
     * Sets the {@link JsonFormat} of this instance
     *
     * @param format the format
     * @return current json
     */
    public JsonBuilder format(JsonFormat format) {
        this.format = format;
        return this;
    }

    /**
     * Enables serializing nulls
     *
     * @return current json
     */
    public JsonBuilder serializeNulls(boolean state) {
        this.serializeNulls = state;
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
     * Adds a {@link JsonSerializer} to the cached ones
     *
     * @param serializer the serializer
     * @return current json
     */
    public <T> JsonBuilder addSerializer(Class<T> cls, JsonSerializer<T> serializer) {
        this.serializers.put(cls, serializer);
        return this;
    }

    /**
     * Builds this instance
     */
    public Json build(JsonSerializer<?>... serializers) {
        for (JsonSerializer serializer : serializers) {
            this.addSerializer(serializer.getTypeClass(), serializer);
        }
        return this.build();
    }
    /**
     * Builds this instance
     */
    public Json build() {
        Json json = new SimpleJson(format, serializeNulls, innerClassSerialization, checkSerializersForSubClasses, writeArraysSingleLined, this.serializers);
        SimpleProvider.getInstance().setSerializerModule(json);
        return  json;
    }
}
