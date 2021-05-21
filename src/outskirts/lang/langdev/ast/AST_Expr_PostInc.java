package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PostInc extends AST {

    private AST expr;

    public AST_Expr_PostInc(List<AST> ls) {
        expr = ls.get(0);
        Validate.isTrue(ls.size() == 1);
    }
}
