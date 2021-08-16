package outskirts.lang.langdev;

import java.util.ArrayList;
import java.util.List;

public class Test2 {

    public static void main(String[] args) {

        int i = 1 + args.length;
        if (i > 0) {
            System.out.println("Sth");
        } else {
            System.out.println("None");
        }

        List<String> loc = new ArrayList<>();

        loc.set(0, "abc");

        return;
    }

}
