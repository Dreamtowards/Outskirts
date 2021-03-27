package outskirts.lang.syntax;

import outskirts.lang.compiler.Code;
import outskirts.lang.compiler.Compilable;
import outskirts.lang.interpreter.Evaluable;
import outskirts.lang.interpreter.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Syntax implements Evaluable, Compilable {

    private final List<Syntax> children = new ArrayList<>();

    public Syntax() { }

    public Syntax(List<Syntax> ls) {
        children.addAll(ls);
    }

    public Syntax child(int i) {
        return children.get(i);
    }
    public int size() {
        return children.size();
    }

    public List<Syntax> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        throw new UnsupportedOperationException("Eval Unsupported. "+getClass());
    }

    @Override
    public void compile(Code codebuf) {
        throw new UnsupportedOperationException("Compile Unsupported. "+getClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+children.toString();
    }

    public String asToken() {
        return ((SyntaxToken)this).getToken().text();
    }
}
