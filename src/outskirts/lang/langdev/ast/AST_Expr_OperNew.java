package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_OperNew extends AST_Expr {

    public final String typename;
    public final ASTls args;

    public AST_Expr_OperNew(String typename, ASTls args) {
        this.typename = typename;
        this.args = args;
    }

    public AST_Expr_OperNew(List<AST> ls) {
        this(((AST_Expr_PrimaryVariableName)ls.get(0)).name, (ASTls)ls.get(1));
    }

    @Override
    public String toString() {
        return "(new "+typename+" ("+args.toString()+"))";
    }
}
