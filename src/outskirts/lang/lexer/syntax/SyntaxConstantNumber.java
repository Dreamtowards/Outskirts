package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.Evaluable;
import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Token;

public class SyntaxConstantNumber extends SyntaxToken {

    public SyntaxConstantNumber(Token token) {
        super(token);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return number();
    }

    public float number() {
        return Float.parseFloat(asToken());
    }
}
