import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonComment;
import io.vson.enums.VsonSettings;

import java.io.File;
import java.io.IOException;

public class Test {


    public static void main(String[] args) {
        try {
            VsonObject vsonObject = new VsonObject(new File("test.vson"));
            vsonObject.append("name", "Sandro");
            vsonObject.append("alter", 18);
            vsonObject.append("volljährig", true);

            vsonObject.comment("name", VsonComment.BEHIND_VALUE, "Das ist der Name der Person");
            vsonObject.comment("alter", VsonComment.UNDER_VALUE, "Das ist das Alter der Person");
            vsonObject.comment("volljährig", VsonComment.MULTI_LINE, "Das ist die Volljährigkeit", "Diese ist 'true', wenn die Person", "18 Jahre oder älter ist");
            vsonObject.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
