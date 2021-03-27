package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxVariableDeclarate extends Syntax {

    public SyntaxVariableDeclarate(List<Syntax> ls) {
        super(ls);
    }

    public String name() {
        return child(0).asToken();
    }

    public Syntax init() {
        return child(1);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.declare(name(), init().eval(env));

        return null;
    }
}
