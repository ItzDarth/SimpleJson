
package eu.simplejson.elements;

import eu.simplejson.JsonEntity;
import eu.simplejson.enums.JsonType;
import eu.simplejson.helper.JsonHelper;
import lombok.Getter;

import java.util.*;

@Getter
public class JsonArray extends JsonEntity implements Iterable<JsonEntity> {

    /**
     * All cached json values
     */
    private final List<JsonEntity> values;

    /**
     * Creates a new empty array
     */
    public JsonArray() {
        this.values = new LinkedList<>();
    }

    public JsonArray(JsonArray array, boolean unmodifiable) {
        this.values = unmodifiable ? Collections.unmodifiableList(array.values) : new ArrayList<>(array.values);
    }

    /**
     * Adds a {@link JsonEntity} to this array
     *
     * @param value the entity
     * @return current array
     */
    public JsonArray add(JsonEntity value) {
        if (value == null) {
            value = JsonLiteral.NULL;
        }
        values.add(value);
        return this;
    }

    /**
     * Removes something under a given index
     *
     * @param index the index
     * @return current array
     */
    public JsonArray remove(int index) {
        values.remove(index);
        return this;
    }

    /**
     * The size of this array
     */
    public int size() {
        return values.size();
    }

    /**
     * Checks if this array is empty
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Gets a {@link JsonEntity} at a given index
     *
     * @param index the index
     * @return the entity that was found
     */
    public JsonEntity get(int index) {
        return values.get(index);
    }

    /**
     * The iterator for this array
     */
    public Iterator<JsonEntity> iterator() {
        return values.iterator();
    }

    @Override
    public JsonType jsonType() {
        return JsonType.ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsonArray asJsonArray() {
        return this;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        JsonArray other = (JsonArray) object;
        return values.equals(other.values);
    }

}
