package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.ObjectInstance;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxVariableDeclarate extends Syntax {

    public SyntaxVariableDeclarate(List<Syntax> ls) {
        super(ls);
    }

    public String type() {
        return child(0).asToken();
    }

    public String name() {
        return child(1).asToken();
    }

    public Syntax init() {
        return child(2);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.declare(name(), new ObjectInstance(type(), ((ObjectInstance)init().eval(env)).value ));

        return null;
    }
}
