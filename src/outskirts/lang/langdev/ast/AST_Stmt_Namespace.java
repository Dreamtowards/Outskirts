package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Stmt_Namespace extends AST_Stmt {

    public final AST_Expr name;
    public final List<AST_Stmt> stmts;

    public AST_Stmt_Namespace(AST_Expr name, List<AST_Stmt> stmts) {
        this.name = name;
        this.stmts = stmts;
    }
}
