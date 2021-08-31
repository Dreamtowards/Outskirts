package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Expr_OperNew extends AST_Expr {

    private final AST__Typename typeptr;
    private final List<AST_Expr> args;

    public AST_Expr_OperNew(AST__Typename typeptr, List<AST_Expr> args) {
        this.typeptr = typeptr;
        this.args = args;
    }

    public AST__Typename getTypename() {
        return typeptr;
    }

    public List<AST_Expr> getArguments() {
        return args;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprOperNew(this, p);
    }

    @Override
    public String toString() {
        return "(new "+typeptr+" ("+args.toString()+"))";
    }
}
