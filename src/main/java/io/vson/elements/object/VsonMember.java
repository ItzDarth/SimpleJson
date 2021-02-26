package io.vson.elements.object;

import io.vson.VsonValue;

import java.io.Serializable;
import java.util.Objects;


public class VsonMember implements Serializable {

    private final String name;
    private final VsonValue value;

    public VsonMember(String name, VsonValue value) {
        this.name = name;
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VsonMember that = (VsonMember) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    public String getName() {
        return name;
    }

    public VsonValue getValue() {
        return value;
    }
}
