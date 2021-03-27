package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Token;

public class SyntaxVariableReference extends SyntaxToken {

    public SyntaxVariableReference(Token token) {
        super(token);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return env.get(name());
    }

    public String name() {
        return asToken();
    }
}
