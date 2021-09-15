package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolVariable;

public class AST_Stmt_DefVar extends AST_Stmt implements AST.Modifierable {

    private final AST_Expr type;
    private final String name;
    private final AST_Expr initexpr;  // nullable.

    private final AST__Modifiers modifiers;

    public SymbolVariable sym;

    public AST_Stmt_DefVar(AST_Expr type, String name, AST_Expr initexpr, AST__Modifiers modifiers) {
        this.type = type;
        this.name = name;
        this.initexpr = initexpr;
        this.modifiers = modifiers;
    }

    public AST_Expr getTypeExpression() {
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
