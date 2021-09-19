package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_GenericsArgument extends AST_Expr {

    private final List<AST_Expr> args;

    public AST_Expr_GenericsArgument(List<AST_Expr> args) {
        this.args = args;
    }

    public List<AST_Expr> getArguments() {
        return args;
    }

}
