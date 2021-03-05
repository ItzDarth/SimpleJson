package io.vson.elements.object;

import com.google.gson.JsonElement;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.VsonLiteral;
import io.vson.manage.vson.VsonParser;
import io.vson.tree.VsonTree;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface Objectable {


    default Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                map.put(declaredField.getName(), declaredField.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    default VsonArray asObjectArray() {
        VsonArray vsonArray = new VsonArray();

        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                if (declaredField.get(this) instanceof Map) {
                    vsonArray.append(VsonObject.encode((Map<String, Object>) declaredField.get(this)));
                } else if (declaredField.get(this) instanceof VsonValue) {
                    vsonArray.append((VsonValue) declaredField.get(this));
                } else if (declaredField.get(this) instanceof JsonElement) {
                    VsonParser parser = new VsonParser(declaredField.get(this).toString());
                    vsonArray.append(parser.parse());
                }
            } catch (IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        }
        return vsonArray;
    }

    default <T> T as(Class<T> tClass) {
        return this.asVsonObject().getAs(tClass);
    }

    default VsonArray asArray() {
        VsonArray vsonArray = new VsonArray();

        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                vsonArray.submit(declaredField.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return vsonArray;
    }

    default VsonObject asVsonObject() {
        VsonObject vsonObject = new VsonObject();

        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                VsonTree vsonTree = new VsonTree();
                if (declaredField.get(this) == null) {
                    vsonObject.append(declaredField.getName(), (Object) null);
                } else {
                    if (declaredField.get(this) instanceof Objectable) {
                        vsonObject.append(declaredField.getName(), ((Objectable) declaredField.get(this)).asVsonObject());
                    } else {
                        vsonObject.append(declaredField.getName(), vsonTree.tree(declaredField.get(this)));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return vsonObject;
    }
}
