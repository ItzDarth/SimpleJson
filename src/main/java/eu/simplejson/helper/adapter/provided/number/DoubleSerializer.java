package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class DoubleSerializer extends JsonSerializer<Double> {

    @Override
    public Double deserialize(JsonEntity element) {
        return element.asDouble();
    }

    @Override
    public JsonEntity serialize(Double obj,Json json) {
        return JsonEntity.valueOf(obj);
    }
}
