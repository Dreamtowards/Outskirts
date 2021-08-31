package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Stmt_DefVar extends AST_Stmt implements AST.Modifierable {

    private final AST__Typename type;
    private final String name;
    private final AST_Expr initexpr;  // nullable.

    public AST__Modifiers modifiers;

    public AST_Stmt_DefVar(AST__Typename type, String name, AST_Expr initexpr) {
        this.type = type;
        this.name = name;
        this.initexpr = initexpr;
    }

    public AST__Typename getTypename() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AST_Expr getInitializer() {
        return initexpr;
    }

    @Override
    public AST__Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtDefVar(this, p);
    }

    @Override
    public String toString() {
        return String.format("ast_vardef{%s %s = %s}", type, name, initexpr);
    }
}
