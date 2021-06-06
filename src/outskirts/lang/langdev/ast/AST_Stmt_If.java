package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_If extends AST {

    private final AST condition;
    private final AST thenb;
    private final AST elseb;

    public AST_Stmt_If(List<AST> ls) {
        condition = ls.get(0);
        thenb = ls.get(1);
        elseb = ls.get(2);
        Validate.isTrue(ls.size()==3);
    }
}
