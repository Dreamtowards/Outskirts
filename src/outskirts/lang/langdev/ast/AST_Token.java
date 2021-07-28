package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Token extends AST {

    private final Token token;

    public AST_Token(Token token) {
        this.token = token;
    }

    public static AST_Token composeConnected(List<AST> ls) {
        Token sampF = ((AST_Token)ls.get(0)).token();
        Token sampL = ((AST_Token)ls.get(ls.size()-1)).token();
        // the Composed Text.
        StringBuilder sb = new StringBuilder();
        for (AST a : ls) {
            Token t = ((AST_Token)a).token();
            if (t != sampL)
                Validate.isTrue(t.isConnectedNext());
            sb.append(t.text());
        }
        return new AST_Token(new Token(sb.toString(), sampF.type(), sampF.getLineNumber(), sampF.getCharNumber(), sampL.isConnectedNext()));
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
