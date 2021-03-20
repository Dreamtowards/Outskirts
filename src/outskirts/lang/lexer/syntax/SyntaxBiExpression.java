package outskirts.lang.lexer.syntax;

import outskirts.lang.lexer.Token;

public class SyntaxBiExpression extends Syntax {

    public Syntax left() {
        return child(0);
    }

    public String operator() {
        return ((SyntaxToken)child(1)).getToken().getText();
    }

    public Syntax right() {
        return child(2);
    }

}
