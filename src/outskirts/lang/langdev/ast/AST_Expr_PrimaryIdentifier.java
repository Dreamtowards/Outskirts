package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.symtab.Symbol;

public class AST_Expr_PrimaryIdentifier extends AST_Expr {

    private final String name;

    public Symbol sym;

    public AST_Expr_PrimaryIdentifier(Token token) {
        name = token.content();
    }

    public String getName() {
        return name;
    }


    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprPrimaryIdentifier(this, p);
    }

    @Override
    public String toString() {
        return "`"+name;
    }
}
