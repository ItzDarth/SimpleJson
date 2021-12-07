package eu.simplejson.helper.json;

import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.adapter.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class JsonBuilder {

    /**
     * Creates a new {@link JsonBuilder} instance
     */
    public static JsonBuilder newBuilder() {
        return new JsonBuilder();
    }

    /**
     * Gets the last build instance
     */
    public static Json lastBuild() {
        return lastBuild;
    }

    public static void setLastBuild(Json json) {
        lastBuild = json;
    }

    /**
     * The last built json instance
     */
    private static Json lastBuild;

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

    /**
     * If primitive arrays should be written like this : [1, 2, 3, 4, 5, 6]
     */
    private boolean writeArraysSingleLined;

    /**
     * Extra serializers
     */
    private final Map<Class<?>, JsonSerializer<?>> serializers;

    private JsonBuilder() {
        this.format = JsonFormat.RAW;
        this.serializeNulls = false;
        this.writeArraysSingleLined = false;
        this.serializeSameFieldInstance = 10;
        this.checkSerializersForSubClasses = true;
        this.serializers = new HashMap<>();
    }

    /**
     * Uses the recommended settings
     *
     * @return current json
     */
    public JsonBuilder recommendedSettings() {
        this.format = JsonFormat.FORMATTED;
        this.serializeNulls = true;
        this.writeArraysSingleLined = true;
        this.serializeSameFieldInstance = 10;
        this.checkSerializersForSubClasses = true;
        return this;
    }

    /**
     * Sets the amount of times a field of an object
     * will be serialized if it's the same type as the class
     * (to prevent StackOverFlow)
     *
     * @param times the amount
     * @return current json
     */
    public JsonBuilder serializeSameFieldInstance(int times) {
        this.serializeSameFieldInstance = times;
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
     * Adds a {@link JsonSerializer} to the cached ones
     *
     * @param serializer the serializer
     * @return current json
     */
    public JsonBuilder addSerializer(Class<?> cls, JsonSerializer<?> serializer) {
        this.serializers.put(cls, serializer);
        return this;
    }

    /**
     * Builds this instance
     */
    public Json build() {
        Json json = new SimpleJsonInstance(format, serializeNulls, serializeSameFieldInstance, checkSerializersForSubClasses, writeArraysSingleLined);

        for (Class aClass : this.serializers.keySet()) {
            JsonSerializer jsonSerializer = this.serializers.get(aClass);
            json.registerSerializer(aClass, jsonSerializer);
        }
        lastBuild = json;
        return  json;
    }

}
