package outskirts.lang.g1.syntax;

import outskirts.lang.g1.compiler.CodeBuf;
import outskirts.lang.g1.compiler.Compilable;
import outskirts.lang.g1.interpreter.Evaluable;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Syntax implements Evaluable, Compilable {

    private final List<Syntax> children = new ArrayList<>();

    public Syntax() { }
    public Syntax(List<Syntax> ls) { children.addAll(ls); }


    @Override
    public Object eval(RuntimeEnvironment env) {

        throw new UnsupportedOperationException("Eval Unsupported. "+getClass());
    }

    @Override
    public void compile(CodeBuf buf) {

        throw new UnsupportedOperationException("Compile Unsupported. "+getClass());
    }



    public final <T extends Syntax> T child(int i) {
        return (T)children.get(i);
    }
    public int size() {
        return children.size();
    }

    public List<Syntax> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+children.toString();
    }

    public final String asToken() {
        return ((SyntaxToken)this).getToken().text();
    }
}
