package outskirts.lang.langdev.ast;

public class AST_Expr_PrimaryLiteralFloat extends AST_Expr {

    private final float numFloat;

    public AST_Expr_PrimaryLiteralFloat(float numFloat) {
        this.numFloat = numFloat;
    }
}
