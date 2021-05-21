package outskirts.lang.g1.syntax.ast;

import outskirts.lang.g1.compiler.CodeBuf;
import outskirts.lang.g1.compiler.Compilable;
import outskirts.lang.g1.interpreter.Evaluable;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

public class AST implements Evaluable, Compilable {

    private final List<AST> children = new ArrayList<>();

    public AST() { }
    public AST(List<AST> ls) { this.children.addAll(ls); }

    @Override
    public void compile(CodeBuf codebuf) {

        throw new UnsupportedOperationException("Compile Unsupported. "+getClass());
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        throw new UnsupportedOperationException("Eval Unsupported. "+getClass());
    }


    public AST child(int i) {
        return children.get(i);
    }

    public int size() {
        return children.size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + children;
    }

    public final String asToken() {
        return ((ASTToken)this).getToken().text();
    }
}
