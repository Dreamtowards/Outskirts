package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.ObjectInstance;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxMethodCall extends Syntax {

    public SyntaxMethodCall(List<Syntax> ls) {
        super(ls);
    }

    public SyntaxVariableReference name() {
       return child(0);
    }

    public List<Syntax> arguments() {
        return child(1).children();
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        ObjectInstance funcval = (ObjectInstance)name().eval(env);
        SyntaxMethodDeclarate method = (SyntaxMethodDeclarate) funcval.value;

        List<Syntax> synargs = arguments();
        ObjectInstance[] args = new ObjectInstance[synargs.size()];
        for (int i = 0;i < synargs.size();i++) {
            args[i] = new ObjectInstance("Unimpl_MethodArgument", synargs.get(i).eval(env));
        }

        return SyntaxMethodDeclarate.call(method, env, args);
    }
}
