package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_OperSizeOf extends AST_Expr {

    public final AST__Typename type;

    public AST_Expr_OperSizeOf(AST__Typename type) {
        this.type = type;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprSizeOf(this, p);
    }
}
