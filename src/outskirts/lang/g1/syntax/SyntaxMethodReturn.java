package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxMethodReturn extends Syntax {

    public SyntaxMethodReturn(List<Syntax> ls) {
        super(ls);
    }

    public Syntax ret() {
        return child(0);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        throw new SyntaxMethodDeclarate.MethodReturnException(ret().eval(env));
    }
}
