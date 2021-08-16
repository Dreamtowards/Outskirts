package outskirts.lang.langdev.interpreter.ex;

import outskirts.lang.langdev.interpreter.GObject;

public class FuncReturnEx extends RuntimeException {

    public final GObject retval;

    public FuncReturnEx(GObject ret) {
        this.retval = ret;
    }

}
