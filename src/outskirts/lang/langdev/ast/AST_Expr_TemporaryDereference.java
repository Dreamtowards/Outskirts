package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Expr_TemporaryDereference extends AST_Expr {

    private AST__Typename typename;
    private AST_Expr expr;

    public AST_Expr_TemporaryDereference(AST__Typename typename, AST_Expr expr) {
        this.typename = typename;
        this.expr = expr;
    }

    public AST__Typename getTypename() {
        return typename;
    }

    public AST_Expr getExpression() {
        return expr;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprTmpDereference(this, p);
    }
}
