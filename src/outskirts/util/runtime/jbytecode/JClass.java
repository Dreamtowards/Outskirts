package outskirts.util.runtime.jbytecode;

import outskirts.util.IOUtils;
import outskirts.util.Validate;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static outskirts.util.IOUtils.*;

// todo: readShort(is) & 0xFFFF;  ->  readUShort(is); / readUnsignedShort(is);
public class JClass {

    public static final int MAGICNUMBER = 0xCAFEBABE;

    private int minorVersion;
    private int majorVersion;

    private List<CONSTANT> constantpool = new ArrayList<>();

    public JClass(InputStream is) throws IOException {
        // magic_number.
        int magin = readInt(is);  assert magin == MAGICNUMBER;

        // versions.
        minorVersion = readShort(is) & 0xFFFF;
        majorVersion = readShort(is) & 0xFFFF;

        // constant_pool
        int cp_sz = (readShort(is) & 0xFFFF) - 1;
        for (int i = 0;i < cp_sz;i++) {
            int tag = readByte(is) & 0xFF;

            CONSTANT cinfo = CONSTANT.byTag(tag);
            cinfo.read(is);

            constantpool.add(cinfo);
        }

        // access_flags.

    }




}
