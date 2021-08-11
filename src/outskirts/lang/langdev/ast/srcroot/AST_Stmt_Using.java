package outskirts.lang.langdev.ast.srcroot;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Expr_OperBi;
import outskirts.lang.langdev.ast.AST_Stmt;

import java.util.List;

public class AST_Stmt_Using extends AST_Stmt {


    public final boolean isStatic;
    public final AST_Expr used;  // OperBin or VarName

    public AST_Stmt_Using(boolean isStatic, AST_Expr used) {
        this.isStatic = isStatic;
        this.used = used;
    }

}
