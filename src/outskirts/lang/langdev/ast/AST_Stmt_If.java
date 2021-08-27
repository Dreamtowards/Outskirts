package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Stmt_If extends AST_Stmt {

    public final AST_Expr condition;
    public final AST_Stmt thenb;
    public final AST_Stmt elseb;  // nullable.

    public AST_Stmt_If(AST_Expr condition, AST_Stmt thenb, AST_Stmt elseb) {
        this.condition = condition;
        this.thenb = thenb;
        this.elseb = elseb;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtIf(this, p);
    }
}
