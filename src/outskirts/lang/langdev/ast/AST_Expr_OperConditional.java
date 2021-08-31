package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

/**
 * Trinary Conditional Expression.
 * condition ? then : else
 */
public class AST_Expr_OperConditional extends AST_Expr {

    private final AST_Expr condition;
    private final AST_Expr exprthen;
    private final AST_Expr exprelse;

    public AST_Expr_OperConditional(AST_Expr condition, AST_Expr exprthen, AST_Expr exprelse) {
        this.condition = condition;
        this.exprthen = exprthen;
        this.exprelse = exprelse;
    }

    public AST_Expr getCondition() {
        return condition;
    }
    public AST_Expr getTrueExpression() {
        return exprthen;
    }
    public AST_Expr getFalseExpression() {
        return exprelse;
    }


    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprOperTriCon(this, p);
    }

    @Override
    public String toString() {
        return "("+condition+" ? "+exprthen+" : "+exprelse+")";
    }
}
