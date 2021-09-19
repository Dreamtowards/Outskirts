package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Stmt_Return extends AST_Stmt {

    private final AST_Expr expr;

    public AST_Stmt_Return(AST_Expr expr) {
        this.expr = expr;
    }

    public AST_Expr getReturnExpression() {
        return expr;
    }

    @Override
    public String toString() {
        return "ast_stmt_return{"+expr+"}";
    }

}
