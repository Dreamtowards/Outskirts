package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.Token;

public class AST_Expr_PrimaryIdentifier extends AST_Expr {

    private final String name;

    public AST_Expr_PrimaryIdentifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "`"+name;
    }
}
