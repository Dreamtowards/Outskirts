package outskirts.util.jruntime.jbytecode;

import outskirts.util.CollectionUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.Validate;

import java.io.*;
import java.util.*;

import static outskirts.util.IOUtils.*;

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

    private static final int MAGIC = 0xCAFEBABE;


    private int minorversion;
    private int majorversion;

    private List<JConstant> constants;  // the actually size atleast 1. the idx_0 is constantly null.

    private short accessflags;

    private String classname;
    private String superclassname;

    private List<String> interfaces;

    private List<JClass.JField> fields;
    private List<JClass.JMethod> methods;

    private List<JClass.JAttribute> attributes;

    public JClass(InputStream is) throws IOException {

        // magic
        int magic = readInt(is);
        Validate.isTrue(magic == MAGIC);

        // minor_version, major_version.
        minorversion = readUnsignedShort(is);
        majorversion = readUnsignedShort(is);

        // constant_pool
        int numConstants = readUnsignedShort(is);
        JConstant[] jconstants = new JConstant[numConstants];
        jconstants[0] = null;
        for (int i = 1;i < numConstants;i++) {
            int ctype = readUnsignedByte(is);
            JConstant cinfo = ReflectionUtils.newInstance(JConstant.CONSTANTS.get(ctype));
            cinfo.read(is);

            jconstants[i] = cinfo;
        }
        constants = Arrays.asList(jconstants);

        // access_flags, this_class, super_class.
        accessflags = readShort(is);
        classname = getConstantClassname(readUnsignedShort(is));
        superclassname = getConstantClassname(readUnsignedShort(is));

        // interfaces
        int numInterfaces = readUnsignedShort(is);
        String[] interfs = new String[numInterfaces];
        for (int i = 0;i < numInterfaces;i++) {
            interfs[i] = getConstantClassname(readUnsignedShort(is));
        }
        interfaces = Arrays.asList(interfs);

        // fields
        int numField = readUnsignedShort(is);
        JField[] jfields = new JField[numField];
        for (int i = 0;i < numField;i++) {
            int modifier = readUnsignedShort(is);
            String name = getConstantUTF(readUnsignedShort(is));
            String type = getConstantUTF(readUnsignedShort(is));
            JAttribute[] attribs = JAttribute.readAttribs(is, this);

            jfields[i] = new JField(modifier, name, type, attribs);
        }
        fields = Arrays.asList(jfields);

        // methods
        int numMethods = readUnsignedShort(is);
        JMethod[] jmethods = new JMethod[numMethods];
        for (int i = 0;i < numMethods;i++) {
            int modifier = readUnsignedShort(is);
            String name = getConstantUTF(readUnsignedShort(is));
            String type = getConstantUTF(readUnsignedShort(is));
            JAttribute[] attribs = JAttribute.readAttribs(is, this);

            jmethods[i] = new JMethod(modifier, name, type, attribs);
        }
        methods = Arrays.asList(jmethods);

        // attributes
        attributes = Arrays.asList(JAttribute.readAttribs(is, this));

    }


    public final int getMinorVersion() {
        return minorversion;
    }
    public final int getMajorVersion() {
        return majorversion;
    }

    public final List<JConstant> getConstants() {
        return constants;
    }
    public final String getConstantUTF(int i) {
        return ((JConstant._UTF8)constants.get(i)).s;
    }
    public final String getConstantClassname(int i) {
        return getConstantUTF(((JConstant._Class)constants.get(i)).classname_idx);
    }

    public final String getClassName() {
        return classname;
    }
    public final String getSuperClassName() {
        return superclassname;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public final List<JField> getFields() {
        return fields;
    }
    public final List<JMethod> getMethods() {
        return methods;
    }

    public final List<JAttribute> getAttributes() {
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

    public static class JField {

        public int modifier;
        public String name;
        public String type;
        public List<JAttribute> attributes;

        public JField(int modifier, String name, String type, JAttribute[] attribs) {
            this.modifier = modifier;
            this.name = name;
            this.type = type;
            this.attributes = Arrays.asList(attribs);
        }
    }

    public static class JMethod {

        public int modifier;
        public String name;
        public String descriptor;
        public List<JAttribute> attributes;

        public JMethod(int modifier, String name, String descriptor, JAttribute[] attribs) {
            this.modifier = modifier;
            this.name = name;
            this.descriptor = descriptor;
            this.attributes = Arrays.asList(attribs);
        }
    }

    public static abstract class JAttribute {

        public static final Map<String, Class<JAttribute>> ATTRIBS = CollectionUtils.asMap(
                "ConstantValue", JAttribute.ConstantValue.class,
                "Code", JAttribute.Code.class,
                "StackMapTable", null,
                "Exceptions", JAttribute.Exceptions.class,
                "InnerClasses", null,
                "EnclosingMethod", null,
                "Synthetic", null,
                "Signature", null,
                "SourceFile", JAttribute.SourceFile.class,
                "SourceDebugExtension", null,
                "LineNumberTable", null,
                "LocalVariableTable", null,
                "LocalVariableTypeTable", null,
                "Deprecated", null,
                "RuntimeVisibleAnnotations", null,
                "RuntimeInvisibleAnnotations", null,
                "RuntimeVisibleParameterAnnotations", null,
                "RuntimeInvisibleParameterAnnotations", null,
                "AnnotationDefault", null,
                "BootstrapMethods", null
        );

        public abstract void read(InputStream is, JClass jclass) throws IOException;

        public static class SourceFile extends JAttribute {
            public int sourcefile_idx;  // utf8

            @Override
            public void read(InputStream is, JClass jclass) throws IOException {
                sourcefile_idx = readUnsignedShort(is);
            }
        }

        public static class ConstantValue extends JAttribute {  // for JField.
            public int constantvalue_idx;

            @Override
            public void read(InputStream is, JClass jclass) throws IOException {
                constantvalue_idx = readUnsignedShort(is);
            }
        }

        public static class Code extends JAttribute {
            public int maxStack;
            public int maxLocals;
            public int codeLength;  // u4
            public byte[] code;
            public ExceptionHandlerInfo[] exceptiontable;
            public List<JAttribute> attributes;  // LineNumberTable, LocalVariableTable, LocalVariableTypeTable, StackMapTable.

            @Override
            public void read(InputStream is, JClass jclass) throws IOException {

                maxStack = readUnsignedShort(is);
                maxLocals = readUnsignedShort(is);
                codeLength = readInt(is);
                code = readFully(is, new byte[codeLength]);

                int numExceptionHandlers = readUnsignedShort(is);
                exceptiontable = new ExceptionHandlerInfo[numExceptionHandlers];
                for (int i = 0;i < numExceptionHandlers;i++) {
                    ExceptionHandlerInfo ehinfo = new ExceptionHandlerInfo();
                    ehinfo.start_pc = readUnsignedShort(is);
                    ehinfo.end_pc = readUnsignedShort(is);
                    ehinfo.handler_pc = readUnsignedShort(is);
                    ehinfo.catch_type = readUnsignedShort(is);

                    exceptiontable[i] = ehinfo;
                }

                attributes = Arrays.asList(JAttribute.readAttribs(is, jclass));
            }

            public static class ExceptionHandlerInfo {
                public int start_pc, end_pc;
                public int handler_pc;
                public int catch_type;  // idx, CONSTANT_Class_info.
            }
        }

        public static class Exceptions extends JAttribute {
            public int[] exception_idx_table;  // CONSTANT_Class_info.

            @Override
            public void read(InputStream is, JClass jclass) throws IOException {
                int num = readUnsignedShort(is);
                for (int i = 0;i < num;i++) {
                    exception_idx_table[i] = readUnsignedShort(is);
                }
            }
        }

        public static JAttribute[] readAttribs(InputStream is, JClass jclass) throws IOException {
            int numAttr = readUnsignedShort(is);
            JAttribute[] attribs = new JAttribute[numAttr];
            for (int i = 0;i < numAttr;i++) {
                String attrtype = jclass.getConstantUTF(readUnsignedShort(is));
                int attrlen = readInt(is);  // u4
                byte[] attrbytes = readFully(is, new byte[attrlen]);

                JAttribute attrib = ReflectionUtils.newInstance(ATTRIBS.get(attrtype));
                attrib.read(new ByteArrayInputStream(attrbytes), jclass);
                attribs[i] = attrib;
            }
            return attribs;
        }
    }

    public static abstract class JConstant {

        public static final Map<Integer, Class<JConstant>> CONSTANTS = CollectionUtils.asMap(
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


        public static final class _UTF8 extends JConstant {
            public String s;

            @Override
            public void read(InputStream is) throws IOException {
                int len = readUnsignedShort(is);
                s = readUTF(is, len);
            }
            @Override public String toString() { return "UTF8{"+s+'}'; }
        }
        public static final class _Integer extends JConstant {
            public int i;

            @Override
            public void read(InputStream is) throws IOException {
                i = readInt(is);
            }
            @Override public String toString() { return "Integer{"+i+'}'; }
        }
        public static final class _Float extends JConstant {
            private float f;

            @Override
            public void read(InputStream is) throws IOException {
                f = readFloat(is);
            }
            @Override public String toString() { return "Float{"+f+'}'; }
        }
        public static final class _Long extends JConstant {
            private long l;

            @Override
            public void read(InputStream is) throws IOException {
                l = readLong(is);
            }
            @Override public String toString() { return "Long{"+l+'}'; }
        }
        public static final class _Double extends JConstant {
            private double d;

            @Override
            public void read(InputStream is) throws IOException {
                d = readDouble(is);
            }
            @Override public String toString() { return "Double{"+d+'}'; }
        }

        public static final class _Class extends JConstant {
            public int classname_idx;  // classname_idx: _UTF8

            @Override
            public void read(InputStream is) throws IOException {
                classname_idx = readUnsignedShort(is);
            }
            @Override public String toString() { return "Class{#"+classname_idx+'}'; }
        }
        public static final class _String extends JConstant {
            private int i;

            @Override
            public void read(InputStream is) throws IOException {
                i = readUnsignedShort(is);
            }
            @Override public String toString() { return "String{#"+i+'}'; }
        }
        public static final class _Fieldref extends JConstant {
            private int class_idx, nameAndType_idx;

            @Override
            public void read(InputStream is) throws IOException {
                class_idx = readUnsignedShort(is);
                nameAndType_idx = readUnsignedShort(is);
            }
            @Override public String toString() { return "Fieldref{cls#"+class_idx+",nameandtype#"+nameAndType_idx+"}"; }
        }
        public static final class _Methodref extends JConstant {
            private int classIdx, nameAndTypeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                classIdx = readUnsignedShort(is);
                nameAndTypeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "Methodref{cls#"+classIdx+",nameandtype#"+nameAndTypeIdx+"}"; }
        }
        public static final class _InterfaceMethodref extends JConstant {
            private int classIdx, nameAndTypeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                classIdx = readUnsignedShort(is);
                nameAndTypeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "InterfaceMethodref{cls#"+classIdx+",nameandtype#"+nameAndTypeIdx+"}"; }
        }
        public static final class _NameAndType extends JConstant {
            private int nameIdx, typeIdx;

            @Override
            public void read(InputStream is) throws IOException {
                nameIdx = readUnsignedShort(is);
                typeIdx = readUnsignedShort(is);
            }
            @Override public String toString() { return "NameAndType{name#"+nameIdx+",type#"+typeIdx+"}"; }
        }
    }
}
