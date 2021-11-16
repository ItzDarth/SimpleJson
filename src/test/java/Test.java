import eu.simplejson.helper.config.JsonConfig;
import eu.simplejson.helper.config.JsonSection;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.json.JsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {

        Json json = JsonBuilder.newBuilder().recommendedSettings().build();


        Map<String, Object> map = new HashMap<>();

        Map<String, Object> map2 = new HashMap<>();
        map2.put("age", 23);
        map2.put("name", "Lystx");
        map.put("newMap", map2);
        map.put("version", 1.0);

        System.out.println(json.toJson(map));
    }




}
