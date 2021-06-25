package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

public class AST_Expr_OperTriCon extends AST {

    private final AST condition;
    private final AST exprthen;
    private final AST exprelse;

    public AST_Expr_OperTriCon(AST condition, AST exprthen, AST exprelse) {
        this.condition = condition;
        this.exprthen = exprthen;
        this.exprelse = exprelse;
    }

    public AST_Expr_OperTriCon(List<AST> ls) {
        this(ls.get(0), ls.get(1), ls.get(2));
    }

    @Override
    public GObject eval(Scope scope) {
        return (AST_Stmt_If.isPass(scope,condition) ? exprthen : exprelse).eval(scope);
    }
}
