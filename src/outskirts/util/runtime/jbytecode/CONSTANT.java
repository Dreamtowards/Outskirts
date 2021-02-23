package outskirts.util.runtime.jbytecode;

import outskirts.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static outskirts.util.IOUtils.*;

public abstract class CONSTANT {


    public abstract void read(InputStream is) throws IOException;

    public static CONSTANT byTag(int tag) {
        throw new UnsupportedOperationException();
    }

    public static final class CONSTANT_UTF8 extends CONSTANT {
        public static final int TAG = 1;
        private String s;

        @Override
        public void read(InputStream is) throws IOException {
            int len = readUnsignedShort(is);
            s = readUTF(is, len);
        }
    }

    public static final class CONSTANT_Integer extends CONSTANT {
        public static final int TAG = 3;
        private int i;

        @Override
        public void read(InputStream is) throws IOException {
            i = readInt(is);
        }
    }
    public static final class CONSTANT_Float extends CONSTANT {
        public static final int TAG = 4;
        private float f;

        @Override
        public void read(InputStream is) throws IOException {
            f = readFloat(is);
        }
    }
    public static final class CONSTANT_Long extends CONSTANT {
        public static final int TAG = 5;
        private long l;

        @Override
        public void read(InputStream is) throws IOException {
            l = readLong(is);
        }
    }
    public static final class CONSTANT_Double extends CONSTANT {
        public static final int TAG = 6;
        private double d;

        @Override
        public void read(InputStream is) throws IOException {
            d = readDouble(is);
        }
    }

    public static final class CONSTANT_Class extends CONSTANT {
        public static final int TAG = 7;
        private int i;

        @Override
        public void read(InputStream is) throws IOException {
            i = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_String extends CONSTANT {
        public static final int TAG = 8;
        private int i;

        @Override
        public void read(InputStream is) throws IOException {
            i = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_Fieldref extends CONSTANT {
        public static final int TAG = 9;
        private int iInClass, iNameType;

        @Override
        public void read(InputStream is) throws IOException {
            iInClass = readUnsignedShort(is);
            iNameType = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_Methodref extends CONSTANT {
        public static final int TAG = 10;
        private int iInClass, iNameType;

        @Override
        public void read(InputStream is) throws IOException {
            iInClass = readUnsignedShort(is);
            iNameType = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_InterfaceMethodref extends CONSTANT {
        public static final int TAG = 11;
        private int iInClass, iNameType;

        @Override
        public void read(InputStream is) throws IOException {
            iInClass = readUnsignedShort(is);
            iNameType = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_NameAndType extends CONSTANT {
        public static final int TAG = 12;
        private int iName, iType;

        @Override
        public void read(InputStream is) throws IOException {
            iName = readUnsignedShort(is);
            iType = readUnsignedShort(is);
        }
    }
    public static final class CONSTANT_MethodHandle extends CONSTANT {
        public static final int TAG = 15;

        @Override
        public void read(InputStream is) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
    public static final class CONSTANT_MethodType extends CONSTANT {
        public static final int TAG = 16;

        @Override
        public void read(InputStream is) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
    public static final class CONSTANT_InvokeDynamic extends CONSTANT {
        public static final int TAG = 18;

        @Override
        public void read(InputStream is) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

}
