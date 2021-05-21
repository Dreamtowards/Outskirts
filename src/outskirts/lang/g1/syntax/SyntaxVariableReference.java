package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.lang.langdev.lexer.Token;

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
