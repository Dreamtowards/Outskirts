package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.parser.RuleLs;
import outskirts.util.Val;
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
        if (size() == 2) return null;
        return child(2);
    }

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
