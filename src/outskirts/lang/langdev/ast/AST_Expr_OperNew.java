package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_OperNew extends AST_Expr {

    public final AST__Typename typeptr;
    public final List<AST_Expr> args;

    public AST_Expr_OperNew(AST__Typename typeptr, List<AST_Expr> args) {
        this.typeptr = typeptr;
        this.args = args;
    }

    @Override
    public String toString() {
        return "(new "+typeptr+" ("+args.toString()+"))";
    }
}
