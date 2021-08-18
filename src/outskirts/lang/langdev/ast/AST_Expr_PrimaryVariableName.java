package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PrimaryVariableName extends AST_Expr {

    public final String name;

    public AST_Expr_PrimaryVariableName(Token token) {
        Validate.isTrue(token.isName());
        name = token.text();
    }

    @Override
    public String toString() {
        return "`"+name;
    }
}
