import com.google.gson.Gson;
import io.vson.VsonValue;
import io.vson.annotation.other.Vson;
import io.vson.annotation.other.VsonAdapter;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import io.vson.manage.vson.VsonWriter;

import java.io.File;
import java.io.IOException;

public class Test {


    public static void main(String[] args) {
        Vson.get().registerAdapter(new MenschAdapter());
        try {
            VsonObject object = new VsonObject(new File("test.vson"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            object.append("testMensch", new Mensch("Hans", 54, new Gson()));

            object.save();

            Mensch mensch = object.getObject("testMensch", Mensch.class);
            System.out.println(mensch.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class MenschAdapter implements VsonAdapter<Mensch> {

        @Override
        public VsonValue write(Mensch mensch, VsonWriter vsonWriter) {
            VsonObject vsonObject = new VsonObject();
            vsonObject.append("name", mensch.getName());
            vsonObject.append("alter", mensch.getAlter());
            vsonObject.append("gson", mensch.gson.getClass().getName());
            return vsonObject;
        }

        @Override
        public Mensch read(VsonValue vsonValue) {
            return null;
        }

        @Override
        public Class<Mensch> getTypeClass() {
            return Mensch.class;
        }
    }


    public static class Mensch {

        private final String name;
        private final int alter;
        private final Gson gson;

        public Mensch(String name, int alter,Gson gson) {
            this.name = name;
            this.alter = alter;
            this.gson = gson;
        }

        public String getName() {
            return name;
        }

        public int getAlter() {
            return alter;
        }

    }
}
