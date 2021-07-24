package outskirts.lang.langdev.ast.srcroot;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr_OperBi;

import java.util.List;

public class AST_SR_Stmt_Using extends AST_SR {

    public final AST_Expr_OperBi used;

    public AST_SR_Stmt_Using(AST_Expr_OperBi used) {
        this.used = used;
    }

    public AST_SR_Stmt_Using(List<AST> ls) {
        this((AST_Expr_OperBi)ls.get(0));
    }

}
