package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxVariableDeclarate extends Syntax {

    public SyntaxVariableDeclarate(List<Syntax> ls) {
        super(ls);
    }

    public String name() {
        return child(0).asToken();
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.varables.put(name(), null);

        return null;
    }
}
