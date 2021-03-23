package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxBlock extends Syntax {

    public SyntaxBlock(List<Syntax> ls) {
        super(ls);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        for (Syntax s : children()) {
            s.eval(env);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Block" + children();
    }
}
