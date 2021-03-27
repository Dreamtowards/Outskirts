package outskirts.lang.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.List;
import java.util.function.Consumer;

public class SyntaxBlock extends Syntax {

    public SyntaxBlock(List<Syntax> ls) {
        super(ls);
    }

    @Override
    public Object eval(RuntimeEnvironment outerenv) {
        return eval0(outerenv, e -> {});
    }

    public Object eval0(RuntimeEnvironment outerenv, Consumer<RuntimeEnvironment> envinit) {
        RuntimeEnvironment env = new RuntimeEnvironment();
        env.outer = outerenv;
        envinit.accept(env);
        execute(env);
        return null;
    }

    public void execute(RuntimeEnvironment env) {
        for (Syntax s : children()) {
            s.eval(env);
        }
    }

    @Override
    public String toString() {
        return "Block" + children();
    }
}
