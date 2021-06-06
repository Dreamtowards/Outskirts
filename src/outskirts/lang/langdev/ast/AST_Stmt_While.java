package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_While extends AST {

    private AST condition;
    private AST then;

    public AST_Stmt_While(List<AST> ls) {
        condition = ls.get(0);
        then = ls.get(1);
        Validate.isTrue(ls.size() == 2);
    }
}
