package outskirts.util.runtime.jbytecode;

import java.io.IOException;
import java.io.InputStream;

public abstract class CONSTANT {


    public abstract void read(InputStream is) throws IOException;

    public static CONSTANT byTag(int tag) {
        throw new UnsupportedOperationException();
    }

    public static final class CONSTANT_UTF8 extends CONSTANT {
        public static final int TAG = 1;
    }

    public static final class CONSTANT_Integer extends CONSTANT {
        public static final int TAG = 3;

    }
    public static final class CONSTANT_Float extends CONSTANT {
        public static final int TAG = 4;

    }
    public static final class CONSTANT_Long extends CONSTANT {
        public static final int TAG = 5;

    }
    public static final class CONSTANT_Double extends CONSTANT {
        public static final int TAG = 6;

    }

    public static final class CONSTANT_Class extends CONSTANT {
        public static final int TAG = 7;

    }
    public static final class CONSTANT_String extends CONSTANT {
        public static final int TAG = 8;

    }
    public static final class CONSTANT_Fieldref extends CONSTANT {
        public static final int TAG = 9;

    }
    public static final class CONSTANT_Methodref extends CONSTANT {
        public static final int TAG = 10;

    }
    public static final class CONSTANT_InterfaceMethodref extends CONSTANT {
        public static final int TAG = 11;

    }
    public static final class CONSTANT_NameAndType extends CONSTANT {
        public static final int TAG = 12;

    }
    public static final class CONSTANT_MethodHandle extends CONSTANT {
        public static final int TAG = 15;

    }
    public static final class CONSTANT_MethodType extends CONSTANT {
        public static final int TAG = 16;

    }
    public static final class CONSTANT_InvokeDynamic extends CONSTANT {
        public static final int TAG = 18;

    }

}
