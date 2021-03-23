package outskirts.lang.lexer.syntax;

import outskirts.lang.lexer.Token;

public class SyntaxToken extends Syntax {

    private Token token;

    public SyntaxToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "" + token + "";
    }

    public static SyntaxToken ofname(String name) {
        return new SyntaxToken(new Token(name, Token.TYPE_NAME, 0, 0));
    }
}
