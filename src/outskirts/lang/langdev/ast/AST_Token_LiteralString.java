package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.lexer.Token;

import java.util.List;

public class AST_Token_LiteralString extends AST_Token {

    private final GObject str;

    public AST_Token_LiteralString(Token token) {
        super(token);
        str = new GObject(token.text());
    }

    public AST_Token_LiteralString(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
    }

    @Override
    public GObject eval(Scope scope) {
        return str;
    }
}
