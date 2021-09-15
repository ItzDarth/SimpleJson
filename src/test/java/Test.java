
import eu.simplejson.JsonEntity;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.CommentType;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.parsers.JsonParser;

public class Test {

    public static void main(String[] args) {
        new Test();
    }

    public Test() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.setFormat(JsonFormat.SIMPLE);
        jsonObject.addProperty("name", "Lystx").comment("name", CommentType.BEHIND_VALUE, "This is the name");

        System.out.println(jsonObject);

    }

}