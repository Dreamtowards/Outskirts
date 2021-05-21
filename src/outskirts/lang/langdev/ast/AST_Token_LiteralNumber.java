package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Token_LiteralNumber extends AST_Token {

    public AST_Token_LiteralNumber(Token token) {
        super(token);
        Validate.isTrue(token.isNumber());
    }

    public AST_Token_LiteralNumber(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public String toString() {
        return text()+"'";
    }
}
