package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.ASTls;

import java.util.List;

public class AST_Annotation extends AST {

    public final AST_Typename type;
    public final AST_Expr[] args;

    public AST_Annotation(AST_Typename type, AST_Expr[] args) {
        this.type = type;
        this.args = args;
    }

    public AST_Annotation(List<AST> ls) {
        this((AST_Typename)ls.get(0), ls.get(1)==null?null: ((ASTls)ls.get(1)).toArrayt(AST_Expr[]::new));
    }
}
