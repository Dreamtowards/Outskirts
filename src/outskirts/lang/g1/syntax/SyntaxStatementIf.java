package outskirts.lang.g1.syntax;

import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

public class SyntaxStatementIf extends Syntax {

    public SyntaxStatementIf(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 2 || ls.size() == 3);
    }

    public Syntax condition() {
        return child(0);
    }

    public Syntax thenblock() {
        return child(1);
    }

    public Syntax elseblock() {
        return child(2);
    }

    /**
     * @param obj evaluated expr result.
     */
    public static boolean isConditionPass(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Number)
            return (float)obj != 0f;
        if (obj instanceof Boolean)
            return (boolean)obj;
        return false;
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        if (isConditionPass(condition().eval(env))) {
            thenblock().eval(env);
        } else {
            elseblock().eval(env);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("IfStat{if: %s, then: %s, else: %s}", condition(), thenblock(), elseblock());
    }
}
