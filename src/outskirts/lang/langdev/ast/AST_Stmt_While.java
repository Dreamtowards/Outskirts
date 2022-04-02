package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_While extends AST_Stmt {

    private final AST_Expr condition;
    private final AST_Stmt then;

    public AST_Stmt_While(AST_Expr condition, AST_Stmt then) {
        this.condition = condition;
        this.then = then;
    }

    public AST_Expr getCondition() {
        return condition;
    }

    public AST_Stmt getStatement() {
        return then;
    }

}
