package outskirts.lang.langdev.ast.srcroot;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Expr_OperBi;

import java.util.List;

public class AST_SR_Stmt_Package extends AST_SR {

    public final AST_Expr name;  // AST_Expr_BiOper (Multiple) or AST_Expr_PrimaryVariableName(Single)

    public AST_SR_Stmt_Package(AST_Expr name) {
        this.name = name;
    }

    public AST_SR_Stmt_Package(List<AST> ls) {
        this((AST_Expr)ls.get(0));
    }

}
