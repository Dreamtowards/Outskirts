package outskirts.lang.langdev.ast.astrecursive;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Stmt;
import outskirts.lang.langdev.ast.AST_Stmt_Block;

public class ASTRecursive {

    public static void walk(AST a) {
        if (a instanceof AST_Stmt) {
            walkStmt((AST_Stmt)a);
        }
    }

    public static void walkStmt(AST_Stmt a) {
        if (a instanceof AST_Stmt_Block) {

        }
    }

}
