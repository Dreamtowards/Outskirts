package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Stmt_Namespace extends AST_Stmt {

    private final AST_Expr name;
    private final List<AST_Stmt> stmts;

    public AST_Stmt_Namespace(AST_Expr name, List<AST_Stmt> stmts) {
        this.name = name;
        this.stmts = stmts;
    }

    public AST_Expr getNameExpression() {
        return name;
    }

    public List<AST_Stmt> getStatements() {
        return stmts;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtNamespace(this, p);
    }
}
