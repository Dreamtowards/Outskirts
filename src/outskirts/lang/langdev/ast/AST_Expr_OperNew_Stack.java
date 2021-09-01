package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Expr_OperNew_Stack {

    private AST__Typename typename;
    private List<AST_Expr> args;

    public AST_Expr_OperNew_Stack(AST__Typename typename, List<AST_Expr> args) {
        this.typename = typename;
        this.args = args;
    }



}
