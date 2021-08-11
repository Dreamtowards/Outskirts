package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.ASTls;

import java.util.List;

public class AST_Annotation extends AST {

    public final AST_Expr type;
    public final List<AST_Expr> args;

    public AST_Annotation(AST_Expr type, List<AST_Expr> args) {
        this.type = type;
        this.args = args;
    }

}
