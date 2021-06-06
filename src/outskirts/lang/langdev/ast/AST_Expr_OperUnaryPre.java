package outskirts.lang.langdev.ast;

import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.List;
import java.util.Map;

public class AST_Expr_OperUnaryPre extends AST {

    private String operator;
    private AST expr;

    public AST_Expr_OperUnaryPre(String operator, AST expr) {
        this.operator = operator;
        this.expr = expr;
    }

    public AST_Expr_OperUnaryPre(List<AST> ls) {
        this(((AST_Token)ls.get(0)).text(), ls.get(1));
        Validate.isTrue(ls.size() == 2);
    }

    @Override
    public String toString() {
        return "{"+operator+expr+"}";
    }

}
