package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;

public interface FuncPtr {

    GObject invoke(GObject[] args);

}
