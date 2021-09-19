package outskirts.lang.langdev.ast;


import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

import java.util.List;

public class AST__Annotation extends AST {

    private final AST_Expr type;
    private final List<AST_Expr> args;

    public AST__Annotation(AST_Expr type, List<AST_Expr> args) {
        this.type = type;
        this.args = args;
    }

    public AST_Expr getNameExpression() {
        return type;
    }

}
