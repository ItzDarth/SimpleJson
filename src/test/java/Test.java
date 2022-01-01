import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.json.JsonBuilder;

public class Test {

    public static void main(String[] args) {

        Json json = new JsonBuilder()
                .format(JsonFormat.SIMPLE)
                .innerClassSerialization(2)
                .serializeNulls(true)
                .writeArraysSingleLined(true)
                .checkSerializersForSubClasses()
                .build();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", json.toJson(null));

        System.out.println(jsonObject);
    }
}
