package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Stmt_If extends AST_Stmt {

    private final AST_Expr condition;
    private final AST_Stmt thenb;
    private final AST_Stmt elseb;  // nullable.

    public AST_Stmt_If(AST_Expr condition, AST_Stmt thenb, AST_Stmt elseb) {
        this.condition = condition;
        this.thenb = thenb;
        this.elseb = elseb;
    }

    public AST_Expr getCondition() {
        return condition;
    }

    public AST_Stmt getThenStatement() {
        return thenb;
    }
    public AST_Stmt getElseStatement() {
        return elseb;
    }

}
