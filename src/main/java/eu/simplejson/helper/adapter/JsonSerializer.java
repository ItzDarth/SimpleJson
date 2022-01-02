package eu.simplejson.helper.adapter;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.json.Json;

import java.lang.reflect.Field;

public abstract class JsonSerializer<T> {

    /**
     * Deserializes an Object from a {@link JsonEntity}
     *
     * @param field the field that is getting serialized (null if the whole object is started with)
     * @param element the element
     * @return serialized object
     */
    public abstract T deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments);


    /**
     * Serializes an object into a {@link JsonEntity}
     *
     * @param field the field that is getting serialized (null if the whole object is started with)
     * @param obj the object
     * @param json the json instance
     * @return serialized element
     */
    public abstract JsonEntity serialize(T obj, Json json, Field field);
}
