package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

public class SyntaxMethodReturn extends Syntax {

    public SyntaxMethodReturn(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 1 || ls.size() == 0);
    }

    public Syntax ret() {
        return size() == 0 ? null : child(0);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        throw new SyntaxMethodDeclarate.MethodReturnException(size() == 0 ? null : ret().eval(env));
    }
}
