package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.Evaluable;
import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Token;
import outskirts.util.Validate;

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
