package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

public class SyntaxMethodDeclarate extends Syntax {

    public SyntaxMethodDeclarate(List<Syntax> ls) {
        super(ls);
    }

    public String name() {
        return child(0).asToken();
    }

    public String[] params() {
        return child(1).children().stream().map(Syntax::asToken).toArray(String[]::new);
    }

    public Syntax body() {
        return child(2);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.varables.put(name(), this);
        return this;
    }

    public static Object call(SyntaxMethodDeclarate smethod, RuntimeEnvironment env, Object... args) {
        String[] params = smethod.params();
        Validate.isTrue(args.length == params.length, "Incomplete arguments.");
        for (int i = 0;i < params.length;i++) {
            env.varables.put(params[i], args[i]);
        }
        try {
            smethod.body().eval(env);
        } catch (MethodReturnException e) {
            return e.obj;
        }
        return null;
    }

    public static class MethodReturnException extends RuntimeException {
        private Object obj;
        public MethodReturnException(Object obj) {
            this.obj = obj;
        }
    }
}
