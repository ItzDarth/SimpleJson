package io.vson.tree;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VsonTree {

    private final Object object;

    public VsonTree() {
        this(null);
    }

    public VsonTree(Object object) {
        this.object = object;
    }

    public <T> T unTree(VsonValue vsonValue, Class<T> tClass) {
        JsonElement jsonObject = new JsonParser().parse(vsonValue.toString(FileFormat.JSON));
        return new Gson().fromJson(jsonObject, tClass);
    }

    public VsonValue tree() {
        return this.tree(this.object);
    }

    public VsonValue tree(Object object) {
        JsonElement jsonElement = new Gson().toJsonTree(object);
        try {
            if (object instanceof List) {
                VsonArray vsonArray = new VsonArray();
                ((List<?>) object).forEach(vsonArray::submit);
                return vsonArray;
            } else if (object instanceof VsonValue) {
                return (VsonValue) object;
            } else if (object instanceof Map) {
                VsonObject vsonObject = new VsonObject();
                ((Map<?, ?>) object).forEach((key, value) -> {
                    vsonObject.append(key.toString(), value);
                });
                return vsonObject;
            }
            return new VsonObject(jsonElement.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getObject() {
        return object;
    }
}
