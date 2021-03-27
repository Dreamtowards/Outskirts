package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxMethodCall extends Syntax {

    public SyntaxMethodCall(List<Syntax> ls) {
        super(ls);
    }

    public String name() {
       try {
           return child(0).asToken();
       } catch (Exception ex) {
           ex.printStackTrace();
           System.exit(0);
           return null;
       }
    }

    public List<Syntax> arguments() {
        return child(1).children();
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        SyntaxMethodDeclarate method = (SyntaxMethodDeclarate)env.get(name());

        List<Syntax> synargs = arguments();
        Object[] args = new Object[synargs.size()];
        for (int i = 0;i < synargs.size();i++) {
            args[i] = synargs.get(i).eval(env);
        }

        return SyntaxMethodDeclarate.call(method, env, args);
    }
}
