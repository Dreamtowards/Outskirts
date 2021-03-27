package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Token;

public class SyntaxConstantString extends SyntaxToken {

    public SyntaxConstantString(Token token) {
        super(token);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return string();
    }

    public String string() {
        return asToken();
    }
}
