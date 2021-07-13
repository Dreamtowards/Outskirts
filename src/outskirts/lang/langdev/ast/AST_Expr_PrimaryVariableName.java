package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_PrimaryVariableName extends AST_Expr {

    public final String name;

    public AST_Expr_PrimaryVariableName(Token token) {
        Validate.isTrue(token.isName());
        name = token.text();
    }

    public AST_Expr_PrimaryVariableName(List<AST> ls) {
        this(((AST_Token)ls.get(0)).token());
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public String toString() {
        return "`"+name;
    }
}
