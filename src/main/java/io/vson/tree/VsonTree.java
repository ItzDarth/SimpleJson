package io.vson.tree;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.VsonLiteral;
import io.vson.elements.VsonNumber;
import io.vson.elements.VsonString;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import io.vson.manage.vson.VsonParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VsonTree {

    private final Object object;

    private boolean safe;

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public VsonTree() {
        this(null);
        this.setSafe(true);
    }

    public VsonTree(Object object) {
        this.object = object;
    }

    public <T> T unTree(VsonValue vsonValue, Class<T> tClass) {
        JsonElement jsonObject = new JsonParser().parse(vsonValue.toString(FileFormat.JSON));
        return new Gson().fromJson(jsonObject, tClass);
    }

    public <T> T unTree(VsonValue vsonValue, Type type) {
        JsonElement jsonObject = new JsonParser().parse(vsonValue.toString(FileFormat.JSON));
        return new Gson().fromJson(jsonObject, type);
    }

    public VsonValue tree() {
        if (safe) {
            return this.treeSafe(this.object);
        }
        return this.tree(this.object);
    }

    public VsonValue tree(Object object) {
        VsonValue value;
        if (object instanceof List) {
            VsonArray vsonArray = new VsonArray();
            ((List<?>) object).forEach(vsonArray::submit);
            value =  vsonArray;
        } else if (object instanceof VsonValue) {
            value =  (VsonValue) object;
        } else if (object instanceof Map) {
            VsonObject vsonObject = new VsonObject();
            ((Map<?, ?>) object).forEach((key, v) -> vsonObject.append(key.toString(), v));
            value = vsonObject;
        } else if (object instanceof Number) {
            value = new VsonNumber(Double.parseDouble(String.valueOf(object)));
        } else if (object instanceof String) {
            value = new VsonString((String) object);
        } else if (object instanceof Boolean) {
            value = ((Boolean) object) ? VsonLiteral.TRUE : VsonLiteral.FALSE;
        } else if (object instanceof Enum<?>) {
            value = new VsonString(((Enum<?>) object).name());
        } else if (object instanceof UUID) {
            value = new VsonString(object.toString());
        } else {
            if (object == null) {
                value = VsonLiteral.NULL;
            } else {
                VsonObject vsonObject = new VsonObject();
                for (Field declaredField : object.getClass().getDeclaredFields()) {
                    if (declaredField == null) {
                        continue;
                    }
                    declaredField.setAccessible(true);
                    try {
                        if (declaredField.get(object) == null) {
                            vsonObject.append(declaredField.getName(), VsonLiteral.NULL);
                            continue;
                        }
                        if (declaredField.get(object).equals(object)) {
                            System.out.println("[VSON] Couldn't serialize obejct from class " + object.getClass().getSimpleName() + " please report to @Lystx!");
                            continue;
                        }
                        vsonObject.append(declaredField.getName(), declaredField.get(object));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                value = vsonObject;
            }
        }
        return value;
    }

    @Deprecated
    public VsonValue treeSafe(Object object) {
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
            return new VsonParser(jsonElement.toString()).parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getObject() {
        return object;
    }
}
