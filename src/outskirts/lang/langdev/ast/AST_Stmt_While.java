package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_While extends AST_Stmt {

    public final AST_Expr condition;
    public final AST_Stmt then;

    public AST_Stmt_While(AST_Expr condition, AST_Stmt then) {
        this.condition = condition;
        this.then = then;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtWhile(this, p);
    }
}
