package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_PrimaryLiteralChar extends AST_Expr {

    private final char numUInt16;

    public AST_Expr_PrimaryLiteralChar(char numUInt16) {
        this.numUInt16 = numUInt16;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprPrimaryLiteralChar(this, p);
    }
}
