import eu.simplejson.helper.config.JsonConfig;
import eu.simplejson.helper.config.JsonSection;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.json.JsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {

        Json json = JsonBuilder.newBuilder().recommendedSettings().build();

        JsonConfig config = json.loadConfig(new File("test.json"));

        config.set("network.players.Lystx.name", "Hans");

        config.save();
    }




}
