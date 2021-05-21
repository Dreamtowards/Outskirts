package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.ObjectInstance;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    public SyntaxBlock body() {
        return (SyntaxBlock)child(2);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        env.declare(name(), new ObjectInstance("function", this));
        return this;
    }

    public static SyntaxMethodDeclarate ofDirect(String name, Function<Object[], Object> func, String... params) {
        return new SyntaxMethodDeclarate(Arrays.asList(
                SyntaxToken.ofname(name),
                new Syntax(Arrays.asList(CollectionUtils.filli(new SyntaxToken[params.length], i -> SyntaxToken.ofname(params[i])))),
                new SyntaxBlock(Collections.singletonList(new SyntaxBlock(Collections.emptyList()) {
                    @Override
                    public void execute(RuntimeEnvironment env) {
                        Object ret = func.apply(CollectionUtils.filli(new Object[params.length], i -> env.get(params[i])));
                        throw new MethodReturnException(ret);
                    }
                }))
        ));
    }

    public static Object call(SyntaxMethodDeclarate smethod, RuntimeEnvironment outerenv, ObjectInstance... args) {
        String[] params = smethod.params();
        Validate.isTrue(args.length == params.length, "Incorrect arguments count.");
        try {
            smethod.body().eval0(outerenv, benv -> {
                for (int i = 0;i < params.length;i++) {
                    benv.declare(params[i], args[i]);
                }
            });
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
