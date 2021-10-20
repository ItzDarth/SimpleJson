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
import objects.IPlayer;
import objects.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {

        Json helper = JsonBuilder.newBuilder().recommendedSettings().build();

        IPlayer player = new Player("Lystx", UUID.randomUUID(), System.currentTimeMillis(), () -> System.out.println("NICE"));


        JsonEntity entity = helper.toJson(player);

        System.out.println(entity);
    }




}
