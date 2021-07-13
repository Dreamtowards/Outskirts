package outskirts.lang.langdev.ast.ex;

import outskirts.lang.langdev.interpreter.GObject;

public class FuncReturnEx extends RuntimeException {

    public final GObject retval;

    public FuncReturnEx(GObject ret) {
        this.retval = ret;
    }

}
