package io.vson.elements.object;

import com.google.gson.JsonElement;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.VsonLiteral;
import io.vson.manage.vson.VsonParser;
import io.vson.tree.VsonTree;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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


    default <T> T from(VsonObject vsonObject, Class<T> tClass) {
        try {
            for (Constructor<?> declaredConstructor : tClass.getDeclaredConstructors()) {
                List<Object> args = new LinkedList<>();
                for (Class<?> ignored : declaredConstructor.getParameterTypes()) {
                    args.add(null);
                }
                T object = (T) declaredConstructor.newInstance(args.toArray());
                for (VsonMember vsonMember : vsonObject) {
                    Field field = object.getClass().getDeclaredField(vsonMember.getName());
                    field.setAccessible(true);
                    field.set(object, vsonObject.getObject(vsonMember.getName()));
                }
                return object;
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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
