
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;

import java.io.File;
import java.util.UUID;


public class Test {

    public static void main(String[] args) {
        new Test();
    }

    public Test() {

        long start = System.currentTimeMillis();

        JsonObject jsonObject = new JsonObject();



        jsonObject.save(new File("test.json"), JsonFormat.FORMATTED);

        System.out.println((System.currentTimeMillis() - start) + "ms took the operation #1!");

       // System.out.println((System.currentTimeMillis() - start) + "ms took the operation #2!"); //Gson 67ms
    }

}