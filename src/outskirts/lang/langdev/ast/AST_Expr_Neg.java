package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_Neg extends AST {

    private AST expr;

    public AST_Expr_Neg(AST expr) {
        this.expr = expr;
    }

    public AST_Expr_Neg(List<AST> ls) {
        this(ls.get(0));
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public String toString() {
        return "-"+expr;
    }
}
