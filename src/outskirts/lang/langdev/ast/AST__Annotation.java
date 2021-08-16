package outskirts.lang.langdev.ast;


import java.util.List;

public class AST__Annotation extends AST {

    public final AST_Expr type;
    public final List<AST_Expr> args;

    public AST__Annotation(AST_Expr type, List<AST_Expr> args) {
        this.type = type;
        this.args = args;
    }

}
