package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Token_VariableName extends AST_Token {

    public AST_Token_VariableName(Token token) {
        super(token);
        Validate.isTrue(token.isName());
    }

    public AST_Token_VariableName(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public String toString() {
        return "`"+text();
    }
}
