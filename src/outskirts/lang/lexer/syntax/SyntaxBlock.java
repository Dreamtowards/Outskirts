package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;

public class SyntaxBlock extends Syntax {

    public SyntaxBlock(List<Syntax> ls) {
        super(ls);
    }

    @Override
    public Object eval(RuntimeEnvironment outerenv) {
        RuntimeEnvironment env = new RuntimeEnvironment();
        env.outer = outerenv;
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
