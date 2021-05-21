package outskirts.lang.g1.syntax;

import outskirts.lang.g1.compiler.CodeBuf;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.util.Validate;

import java.util.List;
import java.util.Objects;

import static outskirts.lang.g1.compiler.Opcodes.FADD;

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
    public void compile(CodeBuf buf) {
        left().compile(buf);
        right().compile(buf);

        String opr = operator();
        if (opr.equals("+")) {
            buf.append(FADD);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        String opr = operator();
        if (opr.equals(".")) {
            SyntaxClassDeclarate clsobj = (SyntaxClassDeclarate)left().eval(env);
//            return l.selfenv.get(((SyntaxVariableReference)right()).name());
            return right().eval(clsobj.selfenv);
        } else if ("+-*/<>".contains(opr)) {
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
            SyntaxVariableReference var = (SyntaxVariableReference)((left() instanceof SyntaxBiExpression) ? left().eval(env) : left());
            Object val = right().eval(env);

            env.get(var.name()).value = val;
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
