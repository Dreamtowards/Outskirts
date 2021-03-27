package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

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

        env.declare(name(), SyntaxMethodDeclarate.ofDirect(name(), arg -> {
            selfenv = env;
            body().eval0(env, benv -> {
                selfenv = benv;
            });
            return this;
        }));

        return this;
    }
}
