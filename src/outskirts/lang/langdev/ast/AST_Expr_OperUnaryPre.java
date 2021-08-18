package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_OperUnaryPre extends AST_Expr {

    /**
     *  ++, --, +, -, !, ~
     */
    public final String operator;
    public final AST_Expr expr;

    public AST_Expr_OperUnaryPre(String operator, AST_Expr expr) {
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "{"+operator+expr+"}";
    }

}
