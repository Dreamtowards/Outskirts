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

    public static final short ACC_PUBLIC    = 0x0001;
    public static final short ACC_PRIVATE   = 0x0002;
    public static final short ACC_PROTECTED = 0x0004;
    public static final short ACC_STATIC    = 0x0008;
    public static final short ACC_FINAL     = 0x0010;
    public static final short ACC_SUPER     = 0x0020;  //
    public static final short ACC_VOLATILE  = 0x0040;
    public static final short ACC_TRANSTENT = 0x0080;
    public static final short ACC_INTERFACE = 0x0200;  //
    public static final short ACC_ABSTRACT  = 0x0400;  //
    public static final short ACC_SYNCHETIC = 0x1000;
    public static final short ACC_ANNOTATION= 0x2000;  //
    public static final short ACC_ENUM      = 0x4000;

    private static final int IDEN_CAFEBABE = 0xCAFEBABE;


    private int minorVersion;
    private int majorVersion;

    private List<CONSTANT> constantpool = new ArrayList<>();

    private short accessflags;

    public JClass(InputStream is) throws IOException {
        int miden = readInt(is);  assert miden == IDEN_CAFEBABE;  // magic_number.

        // versions.
        minorVersion = readShort(is) & 0xFFFF;
        majorVersion = readShort(is) & 0xFFFF;

        // constant_pool
        int numCp = (readShort(is) & 0xFFFF);
        constantpool.add(null);
        for (int i = 1;i < numCp;i++) {
            int tag = readByte(is) & 0xFF;

            CONSTANT cinfo = CONSTANT.byTag(tag);
            cinfo.read(is);

            constantpool.add(cinfo);
        }

        // access_flags.
        accessflags = readShort(is);

        int clsnameIdx = (readShort(is) & 0xFFFF);

        int superclsnameIdx = (readShort(is) & 0xFFFF);

        int numInterfaces = readShort(is) & 0xFFFF;
        for (int i = 0;i < numInterfaces;i++) {
            int idx = readShort(is) & 0xFFFF;

//            constantpool.get()
        }

        int numField = readShort(is) & 0xFFFF;
        for (int i = 0;i < numField;i++) {
            int modifier = readShort(is) & 0xFFFF;
            int nameIdx = readShort(is) & 0xFFFF;// -1
            int typeIdx = readShort(is) & 0xFFFF; // -1
            int attribN = readShort(is) & 0xFFFF;
            assert attribN == 0;
        }

        int numMethods = readShort(is) & 0xFFFF;
        for (int i = 0;i < numMethods;i++) {
            int modifier = readShort(is) & 0xFFFF;
            int nameIdx = readShort(is) & 0xFFFF;
            int typeIdx = readShort(is) & 0xFFFF;
            int attrN = readShort(is) & 0xFFFF;
            for (int attri = 0;attri < attrN;attri++) {
                int attrNameIdx = readShort(is) & 0xFFFF;
                int attrLen = readShort(is) & 0xFFFF;
                readFully(is, new byte[attrLen]);
            }
        }

        int attrN = readShort(is) & 0xFFFF;
        for (int i = 0;i < attrN;i++) {
            long len = readInt(is) & 0xFFFFFFFFL;

        }

    }


    public int getMinorVersion() {
        return minorVersion;
    }
    public int getMajorVersion() {
        return majorVersion;
    }

    public List<CONSTANT> getConstantPool() {
        return constantpool;
    }
}
