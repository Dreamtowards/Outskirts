package outskirts.lang.langdev.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConstantPool {

    private final List<Constant> constants = new ArrayList<>();

    public ConstantPool() {
        constants.add(Constant.CNull0.INST);
    }

    public int size() {
        return constants.size();
    }
    public List<Constant> ls() {
        return constants;
    }

    public short ensureUtf8(String tx) {
        return ensureConstant(new Constant.CUtf8(tx));
    }
    public short ensureInt32(int i) {
        return ensureConstant(new Constant.CInt32(i));
    }

    private short ensureConstant(Constant c) {
        for (int i = 0;i < constants.size();i++) {
            if (constants.get(i).equals(c))
                return (short)i;
        }
        constants.add(c);
        return (short)(constants.size()-1);
    }

    public Constant get(short i) {
        return constants.get(i);
    }




    public static class Constant {

        public static class CNull0 extends Constant {
            public static final CNull0 INST = new CNull0();
            private CNull0() {}
            @Override public String toString() { return "CNull0"; }
        }

        public static class CUtf8 extends Constant {
            public final String tx;
            public CUtf8(String tx) { this.tx = tx; }
            @Override public boolean equals(Object o) { return o instanceof CUtf8 && ((CUtf8) o).tx.equals(tx); }
            @Override public String toString() { return "CUtf8{"+tx+"}"; }
        }

        public static class CInt32 extends Constant {
            public final int i;
            public CInt32(int i) { this.i = i; }
            @Override public boolean equals(Object o) { return o instanceof CInt32 && ((CInt32) o).i == i; }
            @Override public String toString() { return "CInt32{"+i+"}"; }
        }
    }

    @Override
    public String toString() {
        return "ConstantPool"+constants;
    }
}
