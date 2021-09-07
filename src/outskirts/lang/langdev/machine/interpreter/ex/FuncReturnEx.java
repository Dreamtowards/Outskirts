package outskirts.lang.langdev.machine.interpreter.ex;

import outskirts.lang.langdev.machine.interpreter.GObject;

public class FuncReturnEx extends RuntimeException {

    public final GObject retval;

    public FuncReturnEx(GObject ret) {
        this.retval = ret;
    }

}
