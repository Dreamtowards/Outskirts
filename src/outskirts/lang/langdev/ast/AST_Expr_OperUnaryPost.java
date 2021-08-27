package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_OperUnaryPost extends AST_Expr {

    public final AST_Expr expr;
    public final String operator;

    public AST_Expr_OperUnaryPost(AST_Expr expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprOperUPost(this, p);
    }

    @Override
    public String toString() {
        return "{"+expr+operator+"}";
    }
}
