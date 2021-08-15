package outskirts.lang.langdev.ast;

/**
 * condition ? then : else
 */
public class AST_Expr_OperTriCon extends AST_Expr {

    public final AST_Expr condition;
    public final AST_Expr exprthen;
    public final AST_Expr exprelse;

    public AST_Expr_OperTriCon(AST_Expr condition, AST_Expr exprthen, AST_Expr exprelse) {
        this.condition = condition;
        this.exprthen = exprthen;
        this.exprelse = exprelse;
    }

    @Override
    public String toString() {
        return "("+condition+" ? "+exprthen+" : "+exprelse+")";
    }
}
