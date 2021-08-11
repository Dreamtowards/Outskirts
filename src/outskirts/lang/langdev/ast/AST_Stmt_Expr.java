package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Stmt_Expr extends AST_Stmt {

    public final AST_Expr expr;

    public AST_Stmt_Expr(AST_Expr expr) {
        this.expr = expr;
    }

    public AST_Stmt_Expr(List<AST> ls) {
        this((AST_Expr)ls.get(0));
    }

    @Override
    public String toString() {
        return "SmtmExpr::"+expr + ";";
    }
}
