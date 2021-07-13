package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

public class AST_Stmt_DefVar extends AST_Stmt {

    public final String type;
    public final String name;
    public final AST_Expr initexpr;  // nullable.

    public AST_Stmt_DefVar(String type, String name, AST_Expr initexpr) {
        this.type = type;
        this.name = name;
        this.initexpr = initexpr;
    }

    public AST_Stmt_DefVar(List<AST> ls) {
        this(((AST_Expr_PrimaryVariableName)ls.get(0)).name, ((AST_Expr_PrimaryVariableName)ls.get(1)).name, (AST_Expr)ls.get(2));
    }

    @Override
    public String toString() {
        return String.format("ast_vardef{%s %s = %s}", type, name, initexpr);
    }
}
