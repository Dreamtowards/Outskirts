package outskirts.lang.langdev.compiler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public final class ClassFile {

    public final int version;
    public final ConstantPool constantpool;
    public final String thisclass;       // int   idx
    public final String[] superclasses;  // int[] idxs.
    public final Field[] fields;

    public ClassFile(int version, ConstantPool constantpool, String thisclass, String[] superclasses, Field[] fields) {
        this.version = version;
        this.constantpool = constantpool;
        this.thisclass = thisclass;
        this.superclasses = superclasses;
        this.fields = fields;
    }

    public static class Field {

        public static final short MASK_STATIC = 0x0001;

        public String name;
        public short modifier;
        public String type;

        public Field(String name, short modifier, String type) {
            this.name = name;
            this.modifier = modifier;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", modifier=" + modifier +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ClassFile{" +
                "constantpool=" + constantpool +
                ", thisclass='" + thisclass + '\'' +
                ", superclasses=" + Arrays.toString(superclasses) +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }


//    public static final int CLASS_MAGICN = 0xFFFFFFFF;
//
//    public static void writeClass(ClassFile classfile, OutputStream os) throws IOException {
//        DataOutputStream dos = new DataOutputStream(os);
//
//        dos.writeInt(CLASS_MAGICN);
//        dos.writeInt(classfile.version);
//
//        dos.writeShort(classfile.constantpool.size());  // check overflow.
//        for (ConstantPool.Constant cons : classfile.constantpool.ls()) {
//
//        }
//
//    }
}
