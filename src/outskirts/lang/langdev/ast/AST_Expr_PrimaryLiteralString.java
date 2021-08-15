package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.lexer.Token;

import java.util.List;

public class AST_Expr_PrimaryLiteralString extends AST_Expr {

    public final GObject str;
    public final String strRaw;

    public AST_Expr_PrimaryLiteralString(Token token) {
//        var c = ((Scope)ASTEvaluator._EnsureLoadClass("stl.lang.string").value).clxdef;
//        GObject strobj = ASTEvaluator._NewInstance(c, RuntimeExec.RootScope);
//
//        // Bad Impl.
//        ((Scope)strobj.value).assign("value", new GObject(token.text().toCharArray()));

        this.strRaw = token.text();
        this.str = new GObject(token.text());
    }

}
