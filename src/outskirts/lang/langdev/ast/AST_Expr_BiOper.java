package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_BiOper extends AST {

    private AST left;
    private String operator;
    private AST right;

    public AST_Expr_BiOper(AST left, String operator, AST right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public static AST composite(List<AST> ls) {
        int i = 0;
        AST l = ls.get(i++);
        while (i < ls.size()) {
            AST_Token o = (AST_Token)ls.get(i++);
            AST r = ls.get(i++);
            l = new AST_Expr_BiOper(l, o.text(), r);
        }
        return l;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
