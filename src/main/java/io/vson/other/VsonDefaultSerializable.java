
package io.vson.other;

import io.vson.VsonValue;
import io.vson.enums.VsonType;

public class VsonDefaultSerializable extends VsonValue {

    private final Object value;

    public VsonDefaultSerializable(Object value) {
        this.value = value;
    }

    @Override
    public VsonType getType() {
        return VsonType.OTHER;
    }

    @Override
    public Object asObject() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
