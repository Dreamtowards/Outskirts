package outskirts.lang.g1.compiler;

import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class CodeBuf {

    private List<Byte> bytes = new ArrayList<>();

    public void append(byte b) {

        bytes.add(b);

    }

    public void append(int i) {
        byte[] b = IOUtils.writeInt(new byte[4], 0, i);
        bytes.add(b[0]);
        bytes.add(b[1]);
        bytes.add(b[2]);
        bytes.add(b[3]);
    }

    public byte[] arr() {
        return CollectionUtils.toArrayb(bytes);
    }
}
