package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.storage.dst.DObject;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_OperBi extends AST_Expr {

    public final AST_Expr left;
    public final String operator;
    public final AST_Expr right;

    public AST_Expr_OperBi(AST_Expr left, String operator, AST_Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    public AST_Expr_OperBi(List<AST> ls) {
        this((AST_Expr)ls.get(0), ls.get(1).tokentext(), (AST_Expr)ls.get(2));
        Validate.isTrue(ls.size() == 3);
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
