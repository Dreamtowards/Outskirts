package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.Evaluabe;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

public abstract class AST implements Evaluabe {

    @Override
    public GObject eval(Scope scope) {
        throw new UnsupportedOperationException("Eval unsupported. "+getClass());
    }

    public String tokentext() {
        return ((AST_Token)this).text();
    }

    @Override
    public String toString() {
        return "base_ast";
    }

}
