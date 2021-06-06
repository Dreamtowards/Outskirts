package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_VariableDeclare extends AST {

    private String type;
    private String name;
    private AST init;

    public AST_Stmt_VariableDeclare(String type, String name, AST init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public AST_Stmt_VariableDeclare(List<AST> ls) {
        this(ls.get(0).tokentext(), ls.get(1).tokentext(), ls.size() == 3 ? ls.get(2) : null);
        Validate.isTrue(ls.size()==2 || ls.size()==3);
    }

    @Override
    public GObject eval(Scope scope) {
        GObject v = init.eval(scope);
        scope.declare(name, new GObject(type, v));
        return GObject.VOID;
    }
}
