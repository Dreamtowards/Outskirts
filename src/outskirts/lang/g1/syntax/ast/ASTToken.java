package outskirts.lang.g1.syntax.ast;

import outskirts.lang.langdev.lexer.Token;

public class ASTToken extends AST {

    private Token token;

    public ASTToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
