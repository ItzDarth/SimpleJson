import com.google.gson.Gson;
import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.annotation.SerializedField;
import eu.simplejson.helper.annotation.SerializedObject;
import eu.simplejson.helper.annotation.WrapperClass;
import eu.simplejson.helper.exlude.ExcludeStrategy;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.json.JsonBuilder;
import eu.simplejson.helper.parsers.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {


        Json json = JsonBuilder.newBuilder().recommendedSettings().build();
/*
        Example example = new Example("Luca", UUID.randomUUID(), new SimpleText("Text"));

        JsonEntity entity = json.toJson(example);
*/
        Gson gson = new Gson();
        System.out.println(json.toJson(gson));


        System.out.println("==============");
        System.out.println(new JsonObject(gson.toJson(gson)));

    }


    @Getter @AllArgsConstructor
    public static class Example {

        @SerializedField(name = "example_name", ignore = true)
        private final String name;

        private final UUID uniqueId;

        @SerializedField(wrapperClasses = @WrapperClass(interfaceClass = Text.class, wrapperClass = SimpleText.class))
        private final Text text;

    }


    public interface Text {

        String getValue();

    }

    @Getter @AllArgsConstructor
    public static class SimpleText implements Text {

        private final String value;

    }
}
