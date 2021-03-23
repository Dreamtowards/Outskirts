package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

import static outskirts.lang.lexer.syntax.SyntaxStatementIf.isConditionPass;

public class SyntaxStatementWhile extends Syntax {

    public SyntaxStatementWhile(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 2);
    }

    public Syntax condition() {
        return child(0);
    }

    public Syntax body() {
        return child(1);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        while (true) {
            if (isConditionPass(condition().eval(env))) {
                body().eval(env);
            } else {
                break;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("WhileStat{while: %s, do: %s}", condition(), body());
    }
}
