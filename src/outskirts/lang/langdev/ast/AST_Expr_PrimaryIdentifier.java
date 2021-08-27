package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PrimaryIdentifier extends AST_Expr {

    public final String name;

    public AST_Expr_PrimaryIdentifier(Token token) {
        Validate.isTrue(token.isName());
        name = token.text();
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
