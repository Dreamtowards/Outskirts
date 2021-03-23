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
        String nm = name();
        Validate.isTrue(env.varables.containsKey(nm), "Undeclared variable: "+nm+" ("+getToken().detailString());

        return env.varables.get(nm);
    }

    public String name() {
        return asToken();
    }
}
