package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class LongSerializer extends JsonSerializer<Long> {

    @Override
    public Long deserialize(JsonEntity element) {
        return element.asLong();
    }

    @Override
    public JsonEntity serialize(Long obj,Json json) {
        return JsonEntity.valueOf(obj);
    }
}
