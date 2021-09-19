package outskirts.lang.langdev.ast;

public class AST_Expr_TypeCast extends AST_Expr {

    private final AST_Expr expr;
    private final AST_Expr type;

    public AST_Expr_TypeCast(AST_Expr expr, AST_Expr type) {
        this.expr = expr;
        this.type = type;
    }

    public AST_Expr getExpression() {
        return expr;
    }

    public AST_Expr getType() {
        return type;
    }
}
