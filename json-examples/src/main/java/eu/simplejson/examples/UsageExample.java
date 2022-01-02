package eu.simplejson.examples;

import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.serializer.Json;
import eu.simplejson.serializer.builder.JsonBuilder;

public class UsageExample {

    public static void main(String[] args) {
        new UsageExample();
    }

    public UsageExample() {

        Json json = new JsonBuilder().build();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", "Hans");
        jsonObject.addProperty("age", 56);
        jsonObject.addProperty("verified", true);


        System.out.println(jsonObject.toString());
    }
}
