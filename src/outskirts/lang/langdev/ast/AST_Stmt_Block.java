package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST_Stmt_Block extends AST_Stmt {

    private final List<AST_Stmt> stmts;

    public AST_Stmt_Block(List<AST_Stmt> stmts) {
        this.stmts = stmts;
    }

    public List<AST_Stmt> getStatements() {
        return stmts;
    }

}
