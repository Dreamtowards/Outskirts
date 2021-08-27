package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_OperBi extends AST_Expr {

    public final AST_Expr left;
    public final String operator;
    public final AST_Expr right;

    public AST_Expr_OperBi(AST_Expr left, String operator, AST_Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprOperBin(this, p);
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
