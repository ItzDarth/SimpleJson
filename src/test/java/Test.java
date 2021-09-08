
import eu.simplejson.elements.JsonString;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.Json;

import java.io.File;
import java.util.UUID;


public class Test {

    public static void main(String[] args) {
        new Test();
    }

    public Test() {

        long start = System.currentTimeMillis();

        System.out.println(Json.getInstance().fromJson(new JsonString(UUID.randomUUID().toString()), UUID.class));

        System.out.println((System.currentTimeMillis() - start) + "ms took the operation #1!");

       // System.out.println((System.currentTimeMillis() - start) + "ms took the operation #2!"); //Gson 67ms
    }

}