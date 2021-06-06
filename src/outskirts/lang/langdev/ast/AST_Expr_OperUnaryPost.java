package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_OperUnaryPost extends AST {

    private AST expr;
    private String operator;

    public AST_Expr_OperUnaryPost(AST expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }

    public AST_Expr_OperUnaryPost(List<AST> ls) {
        this(ls.get(0), ((AST_Token)ls.get(1)).text());
        Validate.isTrue(ls.size() == 2);
    }

    @Override
    public String toString() {
        return "{"+expr+operator+"}";
    }
}
