package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

public class AST_Stmt_FuncReturn extends AST {

    private AST expr;

    public AST_Stmt_FuncReturn(AST expr) {
        this.expr = expr;
    }

    public AST_Stmt_FuncReturn(List<AST> ls) {
        this(ls.get(0));
    }

    @Override
    public GObject eval(Scope scope) {

        throw new Return(expr);
    }

    @Override
    public String toString() {
        return "ast_stmt_return{"+expr+"}";
    }

    public static class Return extends RuntimeException {
        public AST expr;
        public Return(AST expr) {
            this.expr = expr;
        }
    }
}
