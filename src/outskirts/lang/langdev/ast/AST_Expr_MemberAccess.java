package outskirts.lang.langdev.ast;

public class AST_Expr_MemberAccess extends AST_Expr {

    public final AST_Expr left;
    public final AST_Expr_PrimaryVariableName right;

    public AST_Expr_MemberAccess(AST_Expr left, AST_Expr_PrimaryVariableName right) {
        this.left = left;
        this.right = right;
        throw new UnsupportedOperationException();
    }

}
