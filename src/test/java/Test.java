import io.vson.elements.object.VsonObject;
import io.vson.elements.object.Objectable;
import io.vson.enums.VsonComment;
import io.vson.enums.VsonSettings;

import java.io.File;
import java.io.IOException;

public class Test {


    public static void main(String[] args) {
        try {
            VsonObject vsonObject = new VsonObject(new File("test.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            vsonObject.comment("int", VsonComment.MULTI_LINE, "das ist ein comment", "lol");

            Mensch mensch = new Mensch("Lystx", "Heeg", new VsonObject().append("yarro", 39).append("alt", false), new Mensch("Mick", "Schmitz", new VsonObject(), null));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static class Mensch implements Objectable {

        private final String name, nachname;
        private final VsonObject entries;
        private final Mensch freund;

        public Mensch(String name, String nachname, VsonObject entries, Mensch freund) {
            this.name = name;
            this.nachname = nachname;
            this.entries = entries;
            this.freund = freund;
        }
    }
}
