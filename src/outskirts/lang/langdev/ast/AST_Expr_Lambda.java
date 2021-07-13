package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.ex.FuncPtr;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_Lambda extends AST_Expr {

    public ASTls params;
    public AST body;  // stmt_block or expr.

    public AST_Expr_Lambda(ASTls params, AST_Stmt body) {
        this.params = params;
        this.body = body;
    }

    public AST_Expr_Lambda(List<AST> ls) {
        this((ASTls)ls.get(0), (AST_Stmt)ls.get(1));
    }


    @Override
    public String toString() {
        return "ast_expr_lambda{"+params+"}";
    }
}
