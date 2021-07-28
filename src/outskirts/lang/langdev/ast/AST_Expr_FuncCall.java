package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AST_Expr_FuncCall extends AST_Expr {

    public final AST_Expr funcptr;
    public final List<AST_Expr> args;  // exprs.

    public AST_Expr_FuncCall(AST_Expr expr, List<AST_Expr> args) {
        this.funcptr = expr;
        this.args = args;
    }

    public AST_Expr_FuncCall(List<AST> ls) {
        this((AST_Expr)ls.get(0), ((ASTls)ls.get(1)).list());
    }


    @Override
    public String toString() {
        return "fcall{"+funcptr+"("+args+")}";
    }

}
