package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_While extends AST_Stmt {

    public final AST_Expr condition;
    public final AST_Stmt then;

    public AST_Stmt_While(AST_Expr condition, AST_Stmt then) {
        this.condition = condition;
        this.then = then;
    }

    public AST_Stmt_While(List<AST> ls) {
        this((AST_Expr)ls.get(0), (AST_Stmt)ls.get(1));
        Validate.isTrue(ls.size() == 2);
    }
}
