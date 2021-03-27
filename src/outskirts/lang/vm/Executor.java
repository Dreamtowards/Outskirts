package outskirts.lang.vm;

import outskirts.lang.compiler.Code;
import outskirts.util.IOUtils;

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;

import static outskirts.lang.compiler.Opcodes.LOAD;
import static outskirts.lang.compiler.Opcodes.STORE;

public class Executor {

    public void exec(byte[] code) {
        LinkedList<Object> opstack = new LinkedList<>();
        Object[] locals = new Object[8];

        int idx = 0;
        while (idx < code.length) {
            byte b = code[idx++];
            switch (b) {
                case LOAD: {
                    byte i = code[idx++];
                    opstack.push(locals[i]);
                    break;
                }
                case STORE: {
                    byte i = code[idx++];
                    locals[i] = opstack.pop();
                    break;
                }
                default: throw new UnsupportedOperationException();
            }
        }


    }

}
