package eu.simplejson.helper.adapter.provided.number;

import eu.simplejson.JsonEntity;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.Json;

public class ByteSerializer extends JsonSerializer<Byte> {

    @Override
    public Byte deserialize(JsonEntity element) {
        return element.asByte();
    }

    @Override
    public JsonEntity serialize(Byte obj,Json json) {
        return JsonEntity.valueOf(obj);
    }
}
