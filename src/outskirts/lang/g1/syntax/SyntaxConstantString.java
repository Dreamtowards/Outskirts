package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.lang.langdev.lexer.Token;

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
