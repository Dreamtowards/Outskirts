package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Stmt_Block extends AST_Stmt {

    public final List<AST_Stmt> stmts;

    public AST_Stmt_Block(List<AST_Stmt> stmts) {
        this.stmts = stmts;
    }

    @Override
    public String toString() {
        return "ast_stmt_block{"+ stmts +'}';
    }
}
