package outskirts.lang.g1.syntax;

import outskirts.lang.g1.compiler.CodeBuf;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.lang.langdev.lexer.Token;

import static outskirts.lang.g1.compiler.Opcodes.FCONST;

public class SyntaxConstantNumber extends SyntaxToken {

    public SyntaxConstantNumber(Token token) {
        super(token);
    }

    @Override
    public Object eval(RuntimeEnvironment env) {
        return number();
    }

    @Override
    public void compile(CodeBuf codebuf) {
        codebuf.append(FCONST);
        codebuf.append(Float.floatToIntBits(number()));
    }

    public float number() {
        return Float.parseFloat(asToken());
    }
}
