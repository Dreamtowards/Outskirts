package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_GenericsArgumented extends AST_Expr {

    private final AST_Expr expr;
    private final List<AST_Expr> args;

    public AST_Expr_GenericsArgumented(AST_Expr expr, List<AST_Expr> args) {
        this.expr = expr;
        this.args = args;
    }

    public AST_Expr getTypeExpression() {
        return expr;
    }

    public List<AST_Expr> getGenericsArguments() {
        return args;
    }

}
