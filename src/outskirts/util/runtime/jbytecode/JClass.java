package outskirts.util.runtime.jbytecode;

import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.Validate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static outskirts.util.IOUtils.*;
import static outskirts.util.logging.Log.LOGGER;

// todo: readShort(is) & 0xFFFF;  ->  readUShort(is); / readUnsignedShort(is);
public class JClass {

    public static final int ACC_PUBLIC    = 0x0001;  // FIELD
    public static final int ACC_PRIVATE   = 0x0002;  // FIELD
    public static final int ACC_PROTECTED = 0x0004;  // FIELD
    public static final int ACC_STATIC    = 0x0008;  // FIELD
    public static final int ACC_FINAL     = 0x0010;  // FIELD
    public static final int ACC_SUPER     = 0x0020;  //
    public static final int ACC_VOLATILE  = 0x0040;  // FIELD
    public static final int ACC_TRANSTENT = 0x0080;  // FIELD
    public static final int ACC_INTERFACE = 0x0200;  //
    public static final int ACC_ABSTRACT  = 0x0400;  //
    public static final int ACC_SYNCHETIC = 0x1000;  // FIELD
    public static final int ACC_ANNOTATION= 0x2000;  //
    public static final int ACC_ENUM      = 0x4000;  // FIELD

    private static final int IDEN_CAFEBABE = 0xCAFEBABE;


    private int minorversion;
    private int majorversion;

    private List<Constant> constants = new ArrayList<>();  // actually size needs -1. the idx0 is constantly null.

    private short accessflags;

    private String classname;
    private String superclassname;

    private List<String> interfaces = new ArrayList<>();

    private List<JClass.Field> fields = new ArrayList<>();
    private List<JClass.Method> methods = new ArrayList<>();

    private List<Attribute> attributes = new ArrayList<>();

    public JClass(InputStream is) throws IOException {
        int miden = readInt(is);  assert miden == IDEN_CAFEBABE;  // magic_number.

        // versions.
        minorversion = readUnsignedShort(is);
        majorversion = readUnsignedShort(is);

        // constant_pool
        int numCp = readUnsignedShort(is);
        constants.add(null);
        for (int i = 1;i < numCp;i++) {
            int tag = readUnsignedByte(is);
            Constant cinfo = ReflectionUtils.newInstance(Constant.TYPES.get(tag));
            cinfo.read(is);

            constants.add(cinfo);
        }

        accessflags = readShort(is);
        classname = getConstantClassname(readUnsignedShort(is));
        superclassname = getConstantClassname(readUnsignedShort(is));

        int numInterfaces = readUnsignedShort(is);
        for (int i = 0;i < numInterfaces;i++) {
            String interfname = getConstantClassname(readUnsignedShort(is));
            interfaces.add(interfname);
        }

        int numField = readUnsignedShort(is);
        for (int i = 0;i < numField;i++) {
            int modifier = readUnsignedShort(is);
            String name = getConstantUTF(readUnsignedShort(is));
            String type = getConstantUTF(readUnsignedShort(is));
            int attribN = readUnsignedShort(is);  assert attribN == 0;

            fields.add(new Field(modifier, name, type));
        }

        int numMethods = readUnsignedShort(is);
        for (int i = 0;i < numMethods;i++) {
            int modifier = readUnsignedShort(is);
            String name = getConstantUTF(readUnsignedShort(is));
            String type = getConstantUTF(readUnsignedShort(is));
            int numAttrib = readUnsignedShort(is);
            for (int attri = 0;attri < numAttrib;attri++) {
                String attribType = getConstantUTF(readUnsignedShort(is));
                int attribLen = readInt(is);
                byte[] attribBytes = readFully(is, new byte[attribLen]);


            }
            methods.add(new Method(modifier, name, type));
        }

        int numAttr = readUnsignedShort(is);
        for (int i = 0;i < numAttr;i++) {
            String type = getConstantUTF(readUnsignedShort(is));
            long len = readUnsignedInt(is);

            Attribute attrib = ReflectionUtils.newInstance(Attribute.TYPES.get(type));
            attrib.read(is);  // or read a byte[] .?
            attributes.add(attrib);
        }

    }


    public int getMinorVersion() {
        return minorversion;
    }
    public int getMajorVersion() {
        return majorversion;
    }

    public List<Constant> getConstants() {
        return constants;
    }
    public String getConstantUTF(int i) {
        return ((Constant._UTF8)constants.get(i)).s;
    }
    public String getConstantClassname(int i) {
        return getConstantUTF(((Constant._Class)constants.get(i)).i);
    }

    public String getClassName() {
        return classname;
    }
    public String getSuperClassName() {
        return superclassname;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public List<Field> getFields() {
        return fields;
    }
    public List<Method> getMethods() {
        return methods;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "JClass{" +
                "minorVersion=" + minorversion +
                ", majorVersion=" + majorversion +
                ", constants=" + constants +
                ", accessflags=" + accessflags +
                ", classname='" + classname + '\'' +
                ", superclassname='" + superclassname + '\'' +
                ", interfaces=" + interfaces +
                ", fields=" + fields +
                ", methods=" + methods +
                ", attributes=" + attributes +
                '}';
    }

    public static class Field {

        public int modifier;
        public String name;
        public String type;

        public Field(int modifier, String name, String type) {
            this.modifier = modifier;
            this.name = name;
            this.type = type;
        }
    }

    public static class Method {

        public int modifier;
        public String name;
        public String descriptor;
        public List<Attrib> attributes = new ArrayList<>();

        public Method(int modifier, String name, String descriptor) {
            this.modifier = modifier;
            this.name = name;
            this.descriptor = descriptor;
        }

        public static abstract class Attrib {

        }

        public static class AttribCode extends Attrib {

        }
    }

    public static abstract class Attribute {

        public static final Map<String, Class<Attribute>> TYPES = CollectionUtils.asMap(
                "SourceFile", AttribSourceFile.class
        );

        public abstract void read(InputStream is) throws IOException;

        public static class AttribSourceFile extends Attribute {
            public int i;

            @Override
            public void read(InputStream is) throws IOException {
                i = readUnsignedShort(is);
            }
        }

    }

    public static abstract class Constant {

        public static final Map<Integer, Class<Constant>> TYPES = CollectionUtils.asMap(
                1, _UTF8.class,
                3, _Integer.class,
                4, _Float.class,
                5, _Long.class,
                6, _Double.class,
                7, _Class.class,
                8, _String.class,
                9, _Fieldref.class,
                10, _Methodref.class,
                11, _InterfaceMethodref.class,
                12, _NameAndType.class
        );

        public abstract void read(InputStream is) throws IOException;


        public static final class _UTF8 extends Constant {
            public String s;

            @Override
            public void read(InputStream is) throws IOException {
                int len = readUnsignedShort(is);
                s = readUTF(is, len);
            }
            @Override public String toString() { return "UTF8{"+s+'}'; }
        }
        public static final class _Integer extends Constant {
            public int i;

            @Override
            public void read(InputStream is) throws IOException {
                i = readInt(is);
            }
            @Override public String toString() { return "Integer{"+i+'}'; }
        }
        public static final class _Float extends Constant {
            private float f;

            @Override
            public void read(InputStream is) throws IOException {
                f = readFloat(is);
            }
            @Override public String toString() { return "Float{"+f+'}'; }
        }
        public static final class _Long extends Constant {
            private long l;

            @Override
            public void read(InputStream is) throws IOException {
                l = readLong(is);
            }
            @Override public String toString() { return "Long{"+l+'}'; }
        }
        public static final class _Double extends Constant {
            private double d;

            @Override
            public void read(InputStream is) throws IOException {
                d = readDouble(is);
            }
            @Override public String toString() { return "Double{"+d+'}'; }
        }

        public static final class _Class extends Constant {
            public int i;  // classname_idx: _UTF8

            @Override
            public void read(InputStream is) throws IOException {
                i = readUnsignedShort(is);
            }
            @Override public String toString() { return "Class{#"+i+'}'; }
        }
        public static final class _String extends Constant {
            private int i;

            @Override
            public void read(InputStream is) throws IOException {
                i = readUnsignedShort(is);
            }
            @Override public String toString() { return "String{#"+i+'}'; }
        }
        public static final class _Fieldref extends Constant {
            private int classIdx, nameAndTypeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                classIdx = readUnsignedShort(is);
                nameAndTypeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "Fieldref{cls#"+classIdx+",nameandtype#"+nameAndTypeIdx+"}"; }
        }
        public static final class _Methodref extends Constant {
            private int classIdx, nameAndTypeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                classIdx = readUnsignedShort(is);
                nameAndTypeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "Methodref{cls#"+classIdx+",nameandtype#"+nameAndTypeIdx+"}"; }
        }
        public static final class _InterfaceMethodref extends Constant {
            private int classIdx, nameAndTypeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                classIdx = readUnsignedShort(is);
                nameAndTypeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "InterfaceMethodref{cls#"+classIdx+",nameandtype#"+nameAndTypeIdx+"}"; }
        }
        public static final class _NameAndType extends Constant {
            private int nameIdx, typeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                nameIdx = readUnsignedShort(is);
                typeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "NameAndType{name#"+nameIdx+",type#"+typeIdx+"}"; }
        }
    }

    public static void main(String[] args) throws IOException {

        JClass clx = new JClass(new FileInputStream("TestClassA.class"));

        LOGGER.info(clx);


    }
}
