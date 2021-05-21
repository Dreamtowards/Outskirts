package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;

import java.util.List;

public class AST_Token_LiteralString extends AST_Token {

    public AST_Token_LiteralString(Token token) {
        super(token);
    }

    public AST_Token_LiteralString(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
    }
}
