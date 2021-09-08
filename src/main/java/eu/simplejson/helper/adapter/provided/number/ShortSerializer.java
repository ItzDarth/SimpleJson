package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class ShortSerializer extends JsonSerializer<Short> {

    @Override
    public Short deserialize(JsonEntity element) {
        return element.asShort();
    }

    @Override
    public JsonEntity serialize(Short obj,Json json) {
        return JsonEntity.valueOf(obj);
    }
}
