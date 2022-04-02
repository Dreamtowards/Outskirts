package outskirts.lang.langdev.machine.interpreter.ex;

import outskirts.lang.langdev.machine.interpreter.GObject;

public interface FuncPtr {

    GObject invoke(GObject[] args);

}
