package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.List;

/**
 * condition ? then : else
 */
public class AST_Expr_OperTriCon extends AST_Expr {

    public final AST_Expr condition;
    public final AST_Expr exprthen;
    public final AST_Expr exprelse;

    public AST_Expr_OperTriCon(AST_Expr condition, AST_Expr exprthen, AST_Expr exprelse) {
        this.condition = condition;
        this.exprthen = exprthen;
        this.exprelse = exprelse;
    }

    public AST_Expr_OperTriCon(List<AST> ls) {
        this((AST_Expr)ls.get(0), (AST_Expr)ls.get(1), (AST_Expr)ls.get(2));
    }

    @Override
    public GObject eval(Scope scope) {
        return (AST_Stmt_Strm_If.isPass(scope,condition) ? exprthen : exprelse).eval(scope);
    }
}
