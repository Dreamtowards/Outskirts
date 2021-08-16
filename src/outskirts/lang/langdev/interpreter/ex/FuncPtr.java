package outskirts.lang.langdev.interpreter.ex;

import outskirts.lang.langdev.interpreter.GObject;

public interface FuncPtr {

    GObject invoke(GObject[] args);

}
