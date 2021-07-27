package outskirts.lang.langdev.interpreter.nstdlib;

import outskirts.lang.langdev.ast.ex.FuncPtr;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.nstdlib.gnlx.glux.WindowPrx;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class _nstdlib {

    public static final Map<String, FuncPtr> INTERNAL_FUNC_TABLE = new HashMap<>();

    public static void register_binding_functions() {
        Reg r = new Reg();

        WindowPrx.doREG(r);

    }


    public static class Reg {
        public void reg(String k, Consumer<SimpleFuncPtr> func) {
            INTERNAL_FUNC_TABLE.put(k, args -> {
                GObject[] rf = {GObject.VOID};
                func.accept(new SimpleFuncPtr() {
                    @Override public GObject arg(int i) { return args[i]; }
                    @Override public void retv(GObject o) {  rf[0] = o; }
                });
                return rf[0];
            });
        }
        public static abstract class SimpleFuncPtr {
            public abstract GObject arg(int i);
            public abstract void retv(GObject o);
        }
    }
}
