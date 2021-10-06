package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

// bytheway.. this is really seems like Expr_FuncCall.
public class AST_Expr_OperNew extends AST_Expr {

    private final AST_Expr type;
    private final List<AST_Expr> args;

    public AST_Expr_OperNew(AST_Expr type, List<AST_Expr> args) {
        this.type = type;
        this.args = args;
    }

    public AST_Expr getTypeExpression() {
        return type;
    }

    public List<AST_Expr> getArguments() {
        return args;
    }

}
