package outskirts.lang.langdev.ast.srcroot;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Stmt;

import java.util.List;

public class AST_Stmt_Package extends AST_Stmt {

    public final AST_Expr name;  // AST_Expr_BiOper (Multiple) or AST_Expr_PrimaryVariableName(Single)

    public AST_Stmt_Package(AST_Expr name) {
        this.name = name;
    }
}
