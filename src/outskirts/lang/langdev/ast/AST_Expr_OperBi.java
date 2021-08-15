package outskirts.lang.langdev.ast;

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
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
