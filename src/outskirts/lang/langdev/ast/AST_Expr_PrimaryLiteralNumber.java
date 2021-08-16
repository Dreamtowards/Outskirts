package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PrimaryLiteralNumber extends AST_Expr {

    public final float rawFl;
    public final GObject num;

    public AST_Expr_PrimaryLiteralNumber(Token token) {
        Validate.isTrue(token.isNumber());

        rawFl = Float.parseFloat(token.text());
        num = new GObject(rawFl);
    }

    @Override
    public String toString() {
        return num+"'";
    }
}
