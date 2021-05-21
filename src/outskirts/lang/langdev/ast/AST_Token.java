package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;
import java.util.function.Function;

public class AST_Token extends AST {

    private Token token;

    public AST_Token(Token token) {
        this.token = token;
    }

    public String text() {
        return token.text();
    }

    public Token token() {
        return token;
    }

    @Override
    public String toString() {
        return "'"+token.text()+"'";
    }
}
