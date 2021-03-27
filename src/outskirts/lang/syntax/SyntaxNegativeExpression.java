package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

public class SyntaxNegativeExpression extends Syntax {

    public SyntaxNegativeExpression(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 1);
    }

    public Syntax expr() {
        return child(0);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return -(float)expr().eval(env);
    }

    @Override
    public String toString() {
        return "neg("+expr()+")";
    }
}
