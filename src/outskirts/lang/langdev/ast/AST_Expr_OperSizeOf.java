package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_OperSizeOf extends AST_Expr {

    private final AST__Typename type;

    public AST_Expr_OperSizeOf(AST__Typename type) {
        this.type = type;
    }

    public AST__Typename getTypename() {
        return type;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprSizeOf(this, p);
    }
}
