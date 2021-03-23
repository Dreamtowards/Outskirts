package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;

public class SyntaxDirectPrint extends Syntax {

    public SyntaxDirectPrint(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 1);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {

        System.out.println(child(0).eval(env));

        return null;
    }
}
