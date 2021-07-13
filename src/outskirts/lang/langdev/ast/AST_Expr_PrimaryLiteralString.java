package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.lexer.Token;

import java.util.List;

public class AST_Expr_PrimaryLiteralString extends AST_Expr {

    public final GObject str;

    public AST_Expr_PrimaryLiteralString(Token token) {
        this.str = new GObject(token.text());
    }

    public AST_Expr_PrimaryLiteralString(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
    }

}
