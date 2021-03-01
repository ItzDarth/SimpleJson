
import io.vson.annotation.other.VsonInstance;

public class Test {

    public static void main(String[] args) {
        new Test().init();
    }


    public void init() {
        VsonInstance.getInstance().registerClass(this);

    }
}
