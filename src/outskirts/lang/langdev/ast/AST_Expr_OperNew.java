package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.oop.AST_Typename;

import java.util.List;

public class AST_Expr_OperNew extends AST_Expr {

    public final AST_Typename typeptr;
    public final ASTls args;

    public AST_Expr_OperNew(AST_Typename typeptr, ASTls args) {
        this.typeptr = typeptr;
        this.args = args;
    }

    public AST_Expr_OperNew(List<AST> ls) {
        this((AST_Typename)ls.get(0), (ASTls)ls.get(1));
    }

    @Override
    public String toString() {
        return "(new "+typeptr+" ("+args.toString()+"))";
    }
}
