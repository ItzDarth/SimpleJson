package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class FloatSerializer extends JsonSerializer<Float> {

    @Override
    public Float deserialize(JsonEntity element) {
        return element.asFloat();
    }

    @Override
    public JsonEntity serialize(Float obj,Json json) {
        return JsonEntity.valueOf(obj);
    }
}
