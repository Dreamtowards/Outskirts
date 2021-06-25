package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
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
    public GObject eval(Scope scope) {
        GObject v = expr.eval(scope);

        float tmp = (float)v.value;
        v.value = tmp+1;

        return new GObject(tmp);
    }

    @Override
    public String toString() {
        return "{"+expr+operator+"}";
    }
}
