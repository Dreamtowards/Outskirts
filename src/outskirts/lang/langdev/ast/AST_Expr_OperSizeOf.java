package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_OperSizeOf extends AST_Expr {

    private final AST_Expr type;

    public AST_Expr_OperSizeOf(AST_Expr type) {
        this.type = type;
    }

    public AST_Expr getTypeExpression() {
        return type;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprSizeOf(this, p);
    }
}
