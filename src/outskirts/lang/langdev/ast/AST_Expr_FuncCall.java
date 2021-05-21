package outskirts.lang.langdev.ast;

import java.util.Arrays;
import java.util.List;

public class AST_Expr_FuncCall extends AST {

    private AST expr;
    private AST[] args;  // exprs.

    public AST_Expr_FuncCall(AST expr, AST[] args) {
        this.expr = expr;
        this.args = args;
    }

    public AST_Expr_FuncCall(List<AST> ls) {
        this(ls.get(0), ls.subList(1, ls.size()).toArray(new AST[0]));
    }

    @Override
    public String toString() {
        return "fcall{"+expr+"("+Arrays.toString(args)+")}";
    }
}
