package outskirts.lang.lexer.syntax;

import outskirts.lang.lexer.Token;

public final class SyntaxToken extends Syntax {

    private Token token;

    public SyntaxToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "SyntaxToken{" + token + '}';
    }
}
