
package eu.simplejson.elements.object;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.Json;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.enums.CommentType;
import eu.simplejson.enums.JsonType;
import eu.simplejson.helper.parsers.json.NormalJsonParser;
import eu.simplejson.helper.parsers.easy.SimpleJsonParser;
import javafx.util.Pair;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class JsonObject extends JsonEntity implements Iterable<JsonEntry> {

    /**
     * The keys
     */
    private List<String> names;

    /**
     * The values for all the keys
     */
    private List<JsonEntity> values;

    /**
     * The hash table for storing values
     */
    private HashIndexTable table;

    /**
     * All comments (TODO: READ COMMENTS)
     */
    private final Map<Integer, Pair<String[], CommentType>> comments;

    /**
     * The headers of this config
     */
    private final List<String> header;

    /**
     * Constructs an empty object
     */
    public JsonObject() {
        this.header = new ArrayList<>();
        this.names = new ArrayList<>();
        this.values = new ArrayList<>();
        this.comments = new ConcurrentHashMap<>();
        this.table = new HashIndexTable();
    }

    /**
     * Constructs a {@link JsonObject} from String input
     *
     * @param input the input to parse
     */
    public JsonObject(String input) {
        this();

        try {
            JsonObject jsonObject = new NormalJsonParser(input).parse().asJsonObject();

            this.table = new HashIndexTable();
            this.names = new LinkedList<>(jsonObject.getNames());
            this.values = new LinkedList<>(jsonObject.getValues());

            for (int i = 0; i < names.size(); i++) {
                table.add(names.get(i), i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link JsonObject} from a given {@link File}
     * It will load the object out of the file by reading it
     *
     * @param file the file
     */
    public JsonObject(File file) {
        this();

        try {
            JsonObject jsonObject = new JsonObject();
            if (file.exists()) {
                jsonObject = new SimpleJsonParser(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))).parse().asJsonObject();
            }

            this.table = new HashIndexTable();
            this.names = new LinkedList<>(jsonObject.getNames());
            this.values = new LinkedList<>(jsonObject.getValues());

            for (int i = 0; i < names.size(); i++) {
                table.add(names.get(i), i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills this {@link JsonObject} with an Object
     * It will be serialized to a {@link JsonObject}
     * and all its values will be set in here

     * @param value the object to fill this json object with
     * @return created object
     */
    public JsonObject fill(Object value) {
        JsonEntity jsonEntity = Json.getInstance().toJson(value);

        if (jsonEntity.isJsonObject()) {
            JsonObject jsonObject = jsonEntity.asJsonObject();

            this.names = jsonObject.getNames();
            this.values = jsonObject.getValues();
            this.table = jsonObject.getTable();
            for (int i = 0; i < names.size(); i++) {
                table.add(names.get(i), i);
            }

            return jsonObject;
        }
        return this;
    }

    public JsonObject addHeader(String line) {
        this.header.add(line);
        return this;
    }

    /**
     * Adds a {@link String} to this {@link JsonObject}
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject addProperty(String key, String value) {
        return this.add(key, JsonEntity.valueOf(value));
    }

    /**
     * Adds a {@link Number} to this {@link JsonObject}
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject addProperty(String key, Number value) {
        return this.add(key, JsonEntity.valueOf(value));
    }

    /**
     * Adds a {@link Boolean} to this {@link JsonObject}
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject addProperty(String key, boolean value) {
        return this.add(key, JsonEntity.valueOf(value));
    }

    /**
     * Adds a custom {@link Object} to this {@link JsonObject}
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject addSerialized(String key, Object value) {
        return this.add(key, Json.getInstance().toJson(value));
    }

    /**
     * Adds a {@link JsonEntity} to this entity
     * (It will be appended under a given key)
     *
     * @param key the key where to store
     * @param value the value to store
     * @return current object
     */
    public JsonObject add(String key, JsonEntity value) {
        table.add(key, names.size());
        names.add(key);
        values.add(value);

        return this;
    }

    /**
     * Sets a {@link JsonEntry} at a given name in this entity
     * If a value already exists it will simply override the value
     *
     * @param key the key where to store
     * @param value the value to store
     * @return current object
     */
    public JsonObject set(String key, JsonEntity value) {
        int index = indexOf(key);
        if (index !=- 1) {
            values.set(index, value);
        } else {
            table.add(key, names.size());
            names.add(key);
            values.add(value);
        }
        return this;
    }

    /**
     * Sets a {@link String} to this {@link JsonObject}
     * If a value already exists it will simply override the value
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject setProperty(String key, String value) {
        return this.set(key, JsonEntity.valueOf(value));
    }

    /**
     * Sets a {@link Number} to this {@link JsonObject}
     * If a value already exists it will simply override the value
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject setProperty(String key, Number value) {
        return this.set(key, JsonEntity.valueOf(value));
    }

    /**
     * Sets a {@link Boolean} to this {@link JsonObject}
     * If a value already exists it will simply override the value
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject setProperty(String key, boolean value) {
        return this.set(key, JsonEntity.valueOf(value));
    }

    /**
     * Sets a custom {@link Object} to this {@link JsonObject}
     * If a value already exists it will simply override the value
     *
     * @param key the key
     * @param value the value to store
     * @return current object
     */
    public JsonObject setSerialized(String key, Object value) {
        return this.set(key, Json.getInstance().toJson(value));
    }

    /**
     * Removes a stored value of this object under a given key
     *
     * @param key the key where its stored
     */
    public void remove(String key) {
        int index = this.indexOf(key);

        //It contains the key
        if (index != -1) {
            table.remove(index);
            names.remove(index);
            values.remove(index);
        }
    }

    /**
     * Gets a raw {@link Object} stored under a given eky
     *
     * @param key the key
     * @return the object or null if nothing matched the default objects
     */
    public Object getObject(String key) {
        JsonEntity value = this.get(key);
        return value.asObject();
    }

    /**
     * Gets a raw {@link JsonEntity} under a stored key
     *
     * @param key the key
     * @return entity or null if not containing
     */
    public JsonEntity get(String key) {
        int index = this.indexOf(key);
        return index !=-1 ? values.get(index) : null;
    }

    /**
     * Gets the index of an object stored under a given key
     *
     * @param key the key of the object stored
     * @return index of object
     */
    public int indexOf(String key) {
        int index = table.get(key);
        if (index != -1 && key.equals(names.get(index))) {
            return index;
        }
        return names.lastIndexOf(key);
    }

    /**
     * Gets an Object stored under a given key as a custom object
     *
     * @param key the key where its stored
     * @param tClass the type class
     * @param <T> the generic
     * @return created object or null if errors occurred
     */
    public <T> T getObject(String key, Class<T> tClass) {
        return Json.getInstance().fromJson(this.get(key), tClass);
    }

    /**
     * Gets this whole {@link JsonObject} as a custom object
     *
     * @param tClass the type class of the object you want
     * @param <T> the generic
     * @return created object or null if errors occurred
     */
    public <T> T getAs(Class<T> tClass) {
        return Json.getInstance().fromJson(this, tClass);
    }

    /**
     * Checks if this object contains a key
     *
     * @param key the key
     * @return boolean
     */
    public boolean has(String key) {
        return this.get(key) != null;
    }

    /**
     * Clears this object
     * and simply removes all keys
     */
    public void clear() {
        this.keySet().forEach(this::remove);
    }

    /**
     * Gets a {@link Integer} stored under a given key
     *
     * @param name the key
     * @return int or default value
     */
    public int getInteger(String name) {
        return !this.has(name) ? -1 : this.get(name).asInt();
    }

    /**
     * Gets a {@link Long} stored under a given key
     *
     * @param name the key
     * @return long or default value
     */
    public long getLong(String name) {
        return !this.has(name) ? -1 : this.get(name).asLong();
    }

    /**
     * Gets a {@link Float} stored under a given key
     *
     * @param name the key
     * @return float or default value
     */
    public float getFloat(String name) {
        return !this.has(name) ? -1 : this.get(name).asFloat();
    }

    /**
     * Gets a {@link Double} stored under a given key
     *
     * @param name the key
     * @return double or default value
     */
    public double getDouble(String name) {
        return !this.has(name) ? -1 : this.get(name).asDouble();
    }

    /**
     * Gets a {@link Short} stored under a given key
     *
     * @param name the key
     * @return short or default value
     */
    public short getShort(String name) {
        return !this.has(name) ? -1 : this.get(name).asShort();
    }

    /**
     * Gets a {@link Byte} stored under a given key
     *
     * @param name the key
     * @return byte or default value
     */
    public byte getByte(String name) {
        return !this.has(name) ? -1 : this.get(name).asByte();
    }

    /**
     * Gets a {@link Boolean} stored under a given key
     *
     * @param name the key
     * @return boolean or default value
     */
    public boolean getBoolean(String name) {
        return this.has(name) && this.get(name).asBoolean();
    }

    /**
     * Gets a {@link String} stored under a given key
     *
     * @param key the key
     * @return String or default value
     */
    public String getString(String key) {
        return !this.has(key) ? null : this.get(key).asString();
    }

    /**
     * Gets a {@link Set} containing all the keys of this object
     */
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < this.size(); i++) {
            keys.add(this.getNames().get(i));
        }
        return keys;
    }

    /**
     * Gets the size of this object
     */
    public int size() {
        return names.size();
    }

    /**
     * Checks if this object is empty
     */
    public boolean isEmpty() {
        return names.isEmpty() && values.isEmpty();
    }

    @Override
    public Iterator<JsonEntry> iterator() {
        Iterator<String> namesIterator = names.iterator();
        Iterator<JsonEntity> valuesIterator = values.iterator();
        return new Iterator<JsonEntry>() {

            public boolean hasNext() {
                return namesIterator.hasNext();
            }

            public JsonEntry next() {
                String name = namesIterator.next();
                JsonEntity value = valuesIterator.next();
                return new JsonEntry(name, value);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Adds a comment to this {@link JsonObject} behind a given value stored
     *
     * @param key the key of the object you want to comment on
     * @param commentType the type of the comment
     * @param comment the lines of the comment
     * @return current comment
     */
    public JsonObject comment(String key, CommentType commentType, String... comment) {
        if (this.get(key) instanceof JsonObject || this.get(key) instanceof JsonArray) {
            throw new UnsupportedOperationException("Comments are not available for JsonObjects and JsonArrays at the moment");
        }
        this.comments.put(this.indexOf(key), new Pair<>(comment, commentType));
        return this;
    }


    @Override
    public JsonType getType() {
        return JsonType.OBJECT;
    }

    @Override
    public boolean isJsonObject() {
        return true;
    }

    @Override
    public JsonObject asJsonObject() {
        return this;
    }

    /**
     * Saves this {@link JsonObject} to a specific {@link File}
     * with a given {@link JsonFormat}
     *
     * @param file the file to save it to
     */
    public void save(File file) {
        try {
            if (!file.exists() && file.createNewFile()) {
                this.save(file);
                return;
            }
            PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
            w.print(this.toString(format));
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
