package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST__Token extends AST {

    private final Token token;

    public AST__Token(Token token) {
        this.token = token;
    }

    public static AST__Token composeConnected(List<AST> ls) {
        Token sampF = ((AST__Token)ls.get(0)).token();
        Token sampL = ((AST__Token)ls.get(ls.size()-1)).token();
        // the Composed Text.
        StringBuilder sb = new StringBuilder();
        for (AST a : ls) {
            Token t = ((AST__Token)a).token();
            if (t != sampL)
                Validate.isTrue(t.isConnectedNext());
            sb.append(t.text());
        }
        return new AST__Token(new Token(sb.toString(), sampF.type(), sampF.getLineNumber(), sampF.getCharNumber(), sampL.isConnectedNext()));
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
