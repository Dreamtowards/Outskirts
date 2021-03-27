package outskirts.lang.compiler;

import java.util.ArrayList;
import java.util.List;

public class Code {

    private List<Byte> bytes = new ArrayList<>();

    public void append(byte b) {

        bytes.add(b);

    }

}
