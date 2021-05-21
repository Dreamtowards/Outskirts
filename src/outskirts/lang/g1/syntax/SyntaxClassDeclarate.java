package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.ObjectInstance;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxClassDeclarate extends Syntax {

    public RuntimeEnvironment selfenv;

    public SyntaxClassDeclarate(List<Syntax> ls) {
        super(ls);
    }

    public String name() {
        return child(0).asToken();
    }

    public SyntaxBlock body() {
        return (SyntaxBlock)child(1);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.declare(name(), new ObjectInstance("class", SyntaxMethodDeclarate.ofDirect(name(), arg -> {
            body().eval0(env, benv -> {
                selfenv = benv;
            });
            return this;
        })));

        return this;
    }
}
