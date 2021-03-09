import io.vson.elements.object.Objectable;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import io.vson.manage.vson.VsonParser;
import io.vson.tree.VsonTree;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Test {


    public static void main(String[] args) {
        try {
            VsonObject vsonObject = new VsonObject(new File("test.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            VsonParser vsonParser = new VsonParser();
            VsonTree vsonTree = new VsonTree();

            Mensch mensch = new Mensch("Luca", Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
            vsonObject.putAll(mensch);

            Mensch newMensch = mensch.from(vsonObject, Mensch.class);

            System.out.println(newMensch.getName());
            System.out.println(newMensch.getBans());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static class Mensch implements Objectable {

        private final String name;
        private final List<UUID> bans;

        public Mensch(String name, List<UUID> bans) {
            this.name = name;
            this.bans = bans;
        }

        public String getName() {
            return name;
        }

        public List<UUID> getBans() {
            return bans;
        }
    }

}
