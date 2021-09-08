package eu.simplejson.helper.adapter;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.Json;

public abstract class JsonSerializer<T> {

    /**
     * Deserializes an Object from a {@link JsonEntity}
     *
     * @param element the element
     * @return serialized object
     */
    public abstract T deserialize(JsonEntity element);

    /**
     * Serializes an object into a {@link JsonEntity}
     *
     * @param obj the object
     * @param json the json instance
     * @return serialized element
     */
    public abstract JsonEntity serialize(T obj, Json json);
}
