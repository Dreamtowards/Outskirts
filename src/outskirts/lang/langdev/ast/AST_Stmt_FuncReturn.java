package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

public class AST_Stmt_FuncReturn extends AST_Stmt {

    public final AST_Expr expr;

    public AST_Stmt_FuncReturn(AST_Expr expr) {
        this.expr = expr;
    }

    public AST_Stmt_FuncReturn(List<AST> ls) {
        this((AST_Expr)ls.get(0));
    }

    @Override
    public String toString() {
        return "ast_stmt_return{"+expr+"}";
    }

}
