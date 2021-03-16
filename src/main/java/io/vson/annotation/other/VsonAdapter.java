package io.vson.annotation.other;

import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.manage.json.JsonWriter;
import io.vson.manage.vson.VsonParser;
import io.vson.manage.vson.VsonWriter;

public interface VsonAdapter<T> {

    VsonValue write(T t, VsonWriter vsonWriter);

    T read(VsonValue vsonValue);

    Class<T> getTypeClass();
}
