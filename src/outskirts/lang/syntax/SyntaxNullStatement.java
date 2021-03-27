package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxNullStatement extends Syntax {

    public SyntaxNullStatement(List<Syntax> ls) {
        super(ls);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return null;
    }
}
