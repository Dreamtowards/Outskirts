package outskirts.lang.langdev.ast;

public class AST_Expr_OperNewMalloc extends AST_Expr {

    private final AST_Expr exprSize;

    public AST_Expr_OperNewMalloc(AST_Expr exprSize) {
        this.exprSize = exprSize;
    }

    public AST_Expr getSizeExpression() {
        return exprSize;
    }
}
