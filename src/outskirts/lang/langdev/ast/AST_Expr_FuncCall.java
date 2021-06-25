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

public class AST_Expr_FuncCall extends AST {

    private AST expr;
    private AST[] args;  // exprs.

    public AST_Expr_FuncCall(AST expr, AST[] args) {
        this.expr = expr;
        this.args = args;
    }

    public AST_Expr_FuncCall(List<AST> ls) {
        this(ls.get(0), ((ASTls)ls.get(1)).toArray());
    }

    @Override
    public GObject eval(Scope scope) {
        GObject funcptr = expr.eval(scope);

        GObject[] argv = new GObject[args.length];
        for (int i = 0;i < args.length;i++) {
            argv[i] = args[i].eval(scope);
        }

        return ((FuncPtr)funcptr.value).invoke(argv);
    }

    @Override
    public String toString() {
        return "fcall{"+expr+"("+Arrays.toString(args)+")}";
    }

}
