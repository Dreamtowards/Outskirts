package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PrimaryLiteralNumber extends AST_Expr {

    public final GObject num;

    public AST_Expr_PrimaryLiteralNumber(Token token) {
        Validate.isTrue(token.isNumber());

        num = new GObject(Float.parseFloat(token.text()));
    }

    public AST_Expr_PrimaryLiteralNumber(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public String toString() {
        return num+"'";
    }
}
