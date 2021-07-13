package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_Strm_If extends AST_Stmt {

    public final AST_Expr condition;
    public final AST_Stmt thenb;
    public final AST_Stmt elseb;  // nullable.

    public AST_Stmt_Strm_If(AST_Expr condition, AST_Stmt thenb, AST_Stmt elseb) {
        this.condition = condition;
        this.thenb = thenb;
        this.elseb = elseb;
    }

    public AST_Stmt_Strm_If(List<AST> ls) {
        this((AST_Expr)ls.get(0), (AST_Stmt)ls.get(1), (AST_Stmt)ls.get(2));
    }

    public static boolean isPass(Scope sc, AST condition) {
        return (float)condition.eval(sc).value != 0;
    }

    @Override
    public GObject eval(Scope scope) {

        if (isPass(scope, condition)) {
            thenb.eval(scope);
        } else {
            elseb.eval(scope);
        }

        return GObject.VOID;
    }
}
