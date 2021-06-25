package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

public class AST_Stmt_DefVar extends AST {

    private String type;
    private String name;
    private AST init;

    public AST_Stmt_DefVar(String type, String name, AST init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public AST_Stmt_DefVar(List<AST> ls) {
        this(ls.get(0).tokentext(), ls.get(1).tokentext(), ls.get(2));
    }

    @Override
    public GObject eval(Scope scope) {
        GObject v = init.eval(scope);
        // where member @type use for.?
        scope.declare(name, v);
        return GObject.VOID;
    }

    @Override
    public String toString() {
        return String.format("ast_vardef{%s %s = %s}", type, name, init);
    }
}
