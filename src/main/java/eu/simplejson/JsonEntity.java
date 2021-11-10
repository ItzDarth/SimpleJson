
package eu.simplejson;

import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonNumber;
import eu.simplejson.elements.JsonString;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.enums.JsonType;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.parsers.WritingBuffer;
import eu.simplejson.helper.parsers.json.NormalJsonWriter;
import eu.simplejson.helper.parsers.easy.SimpleJsonWriter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.UUID;

public abstract class JsonEntity implements Serializable {

    /**
     * The format of this entity
     * changed by {@link eu.simplejson.helper.json.Json} instance
     */
    @Setter
    protected JsonFormat format = JsonFormat.FORMATTED;

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(int value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(long value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(float value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(short value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(byte value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(double value) {
        return new JsonNumber(value);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param string the value
     * @return entity
     */
    public static JsonEntity valueOf(String string) {
        return string == null ? JsonLiteral.NULL :  new JsonString(string);
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(boolean value) {
        return value ? JsonLiteral.TRUE : JsonLiteral.FALSE;
    }

    /**
     * Creates a new {@link JsonEntity} by a default value
     *
     * @param value the value
     * @return entity
     */
    public static JsonEntity valueOf(Object value) {
        if (value instanceof Integer) {
            return valueOf((int) value);
        } else if (value instanceof Long) {
            return valueOf((long) value);
        } else if (value instanceof Double) {
            return valueOf((double) value);
        } else if (value instanceof Byte) {
            return valueOf((byte) value);
        } else if (value instanceof Short) {
            return valueOf((short) value);
        } else if (value instanceof Float) {
            return valueOf((float) value);
        } else if (value instanceof Boolean) {
            return valueOf((boolean) value);
        } else if (value instanceof String) {
            return valueOf((String) value);
        } else {
            return null;
        }
    }

    /**
     * Gets the {@link JsonType} of this entity
     * to identify what type this is
     */
    public abstract JsonType jsonType();

    /**
     * If this json is type for a primitive
     */
    public abstract boolean isPrimitive();

    /**
     * Checks if this {@link JsonEntity} is a {@link JsonObject}
     */
    public boolean isJsonObject() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link JsonArray}
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Number}
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Short}
     */
    public boolean isShort() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Double}
     */
    public boolean isDouble() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Long}
     */
    public boolean isLong() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Byte}
     */
    public boolean isByte() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Integer}
     */
    public boolean isInt() {
        return false;
    }
    
    /**
     * Checks if this {@link JsonEntity} is a {@link Float}
     */
    public boolean isFloat() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link String}
     */
    public boolean isString() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Boolean}
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Boolean} and true
     */
    public boolean isTrue() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is a {@link Boolean} and false
     */
    public boolean isFalse() {
        return false;
    }

    /**
     * Checks if this {@link JsonEntity} is null
     */
    public boolean isNull() {
        return false;
    }

    /**
     * Gets this {@link JsonEntity} as {@link JsonObject}
     */
    public JsonObject asJsonObject() {
        throw new UnsupportedOperationException("Not a JsonObject : "+ toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link JsonArray}
     */
    public JsonArray asJsonArray() {
        throw new UnsupportedOperationException("Not a JsonArray: "+ toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Short}
     */
    public short asShort() {
        throw new UnsupportedOperationException("Not a short: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Byte}
     */
    public byte asByte() {
        throw new UnsupportedOperationException("Not a byte: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Integer}
     */
    public int asInt() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as a custom object of your choice
     *
     * @param typeClass the type class of the object
     * @param <T> the generic
     * @return built object
     */
    public <T> T asCustom(Class<T> typeClass, Json json) {
        return json.fromJson(this, typeClass);
    }

    /**
     * Gets this {@link JsonEntity} as {@link Long}
     */
    public long asLong() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Float}
     */
    public float asFloat() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Double}
     */
    public double asDouble() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link String}
     */
    public String asString() {
        throw new UnsupportedOperationException("Not a string: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link UUID}
     */
    public UUID asUUID() {
        throw new UnsupportedOperationException("Not a uuid: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Boolean}
     */
    public Boolean asBoolean() {
        throw new UnsupportedOperationException("Not a boolean: " + toString());
    }

    /**
     * Gets this {@link JsonEntity} as {@link Object}
     * depending on the different checks this entity has
     * if {@link JsonEntity#isString()} returns true
     * it will return {@link JsonEntity#asString()}
     *
      */
    public Object asObject() {
        if (this instanceof JsonString) {
            return asString();
        } else if (this instanceof JsonNumber) {
            if (this.isFloat()) {
                return this.asFloat();
            } else if (this.isDouble()) {
                return this.asDouble();
            } else if (this.isLong()) {
                return this.asLong();
            } else if (this.isInt()) {
                return this.asInt();
            } else if (this.isByte()) {
                return this.asByte();
            } else if (this.isShort()) {
                return this.asShort();
            }
        } else if (this.isNull()) {
            return null;
        } else if (this instanceof JsonLiteral) {
            JsonLiteral jsonLiteral = (JsonLiteral)this;
            if (jsonLiteral == JsonLiteral.TRUE) {
                return true;
            } else if (jsonLiteral == JsonLiteral.FALSE) {
                return false;
            }
            return null;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.toString(this.format);
    }

    /**
     * Parses this {@link JsonEntity} to a {@link String}
     * with a provided {@link JsonFormat}
     * 
     * @param format the format
     * @return string
     */
    public String toString(JsonFormat format) {
        StringWriter writer = new StringWriter();

        try {
            WritingBuffer buffer = new WritingBuffer(writer, 128);

            if (format != JsonFormat.SIMPLE) {
                NormalJsonWriter jsonWriter = new NormalJsonWriter(format == JsonFormat.FORMATTED);
                jsonWriter.saveRecursive(this, buffer, 0);
            } else {
                SimpleJsonWriter jsonWriter = new SimpleJsonWriter();
                jsonWriter.saveRecursive(this, buffer, 0, "", true);
            }

            buffer.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return writer.toString();
    }

}
