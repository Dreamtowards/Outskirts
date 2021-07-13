package outskirts.lang.langdev.ast.ex;

import outskirts.lang.langdev.interpreter.GObject;

public interface FuncPtr {

    GObject invoke(GObject[] args);

}
