package outskirts.lang.lexer.syntax;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;
import java.util.Objects;

public class SyntaxBiExpression extends Syntax {

    public SyntaxBiExpression(List<Syntax> ls) {
        super(ls);
        Validate.isTrue(ls.size() == 3);
    }

    public Syntax left() {
        return child(0);
    }

    public String operator() {
        return child(1).asToken();
    }

    public Syntax right() {
        return child(2);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        String opr = operator();
        if ("+-*/<>".contains(opr)) {
            Object l = left().eval(env);
            Object r = right().eval(env);
            if (l instanceof String || r instanceof String) {
                return l + "" + r;
            } else {
                float fl = (float)l;
                float fr = (float)r;
                switch (operator()) {
                    case "+": return fl + fr;
                    case "-": return fl - fr;
                    case "*": return fl * fr;
                    case "/": return fl / fr;
                    case "<": return fl < fr;
                    case ">": return fl > fr;
                }
                throw new RuntimeException("Impossible Exception.");
            }
        } else if (opr.equals("=")) {
            SyntaxVariableReference var = (SyntaxVariableReference)left();
            Object val = right().eval(env);
            env.varables.put(var.name(), val);
            return val;
        } else if (opr.equals("==")) {

            return Objects.equals(left().eval(env), right().eval(env));
        } else {
            throw new UnsupportedOperationException("Unsupported Operator. "+opr);
        }
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left(), operator(), right());
    }
}
