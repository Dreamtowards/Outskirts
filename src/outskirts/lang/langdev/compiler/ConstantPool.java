package outskirts.lang.langdev.compiler;

import java.util.ArrayList;
import java.util.List;

public class ConstantPool {

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

    public int ensureUtf8(String tx) {
        for (int i = 0;i < constants.size();i++) {
            Constant c = constants.get(i);
            if (c instanceof Constant.CUtf8) {
                if (((Constant.CUtf8)c).tx.equals(tx)) {
                    return i;
                }
            }
        }

        constants.add(new Constant.CUtf8(tx));
        return constants.size()-1;
    }




    public static class Constant {

        public static class CNull0 extends Constant {
            public static final CNull0 INST = new CNull0();
            private CNull0() {}
            @Override public String toString() { return "CNull0"; }
        }

        public static class CUtf8 extends Constant {
            private String tx;
            public CUtf8(String tx) {
                this.tx = tx;
            }
        }
    }

    @Override
    public String toString() {
        return "ConstantPool"+constants;
    }
}
