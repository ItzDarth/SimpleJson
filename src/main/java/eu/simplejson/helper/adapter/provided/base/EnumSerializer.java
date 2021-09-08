package eu.simplejson.helper.adapter.provided.base;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class EnumSerializer extends JsonSerializer<Enum> {

    @Override
    public Enum deserialize(JsonEntity element) {
        try {
            String s  = element.asString();
            String s1 = s.split("\\.")[0];
            Class enumClass = Class.forName(s1);
            return Enum.valueOf(enumClass, s.split("\\.")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonEntity serialize(Enum obj,Json json) {
        return new JsonString(obj.name());
    }
}
