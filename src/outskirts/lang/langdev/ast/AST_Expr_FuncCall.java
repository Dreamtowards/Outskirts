package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Expr_FuncCall extends AST_Expr {

    public final AST_Expr funcptr;
    public final List<AST_Expr> args;  // exprs.

    public AST_Expr_FuncCall(AST_Expr expr, List<AST_Expr> args) {
        this.funcptr = expr;
        this.args = args;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprFuncCall(this, p);
    }

    @Override
    public String toString() {
        return "fcall{"+funcptr+"("+args+")}";
    }

}
