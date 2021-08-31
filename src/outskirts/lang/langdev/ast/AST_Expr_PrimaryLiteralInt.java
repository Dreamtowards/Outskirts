package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_PrimaryLiteralInt extends AST_Expr {

    private final int numInt;

    public AST_Expr_PrimaryLiteralInt(int numInt) {
        this.numInt = numInt;
    }

    public int getInt32() {
        return numInt;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprPrimaryLiteralInt(this, p);
    }
}
