
import eu.simplejson.JsonEntity;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.CommentType;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.parsers.JsonParser;
import master.MasterConfig;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        new Test();
    }

    public Test() {

        JsonObject jsonObject = new JsonObject(new File("config.json"));

        MasterConfig as = jsonObject.getAs(MasterConfig.class);

        System.out.println(as.getMessages());
        System.out.println("___________");
        System.out.println(as.getProperties());

        System.out.println(as);


    }

}