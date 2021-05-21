package outskirts.lang.g1.vm;

import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static outskirts.lang.g1.compiler.Opcodes.*;

public class Executor {

    public void exec(byte[] code) throws IOException {
        LinkedList<Object> opstack = new LinkedList<>();
        Object[] locals = new Object[8];
        Log.LOGGER.info(Arrays.toString(code));

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
                case FCONST: {
                    float f = IOUtils.readFloat(code, idx);
                    opstack.push(f);
                    idx += 4;
                    break;
                }
                case FADD: {
                    float f2 = (float)opstack.pop();
                    float f1 = (float)opstack.pop();
                    float v = f1 + f2;
                    opstack.push(v);
                    break;
                }
                default: throw new UnsupportedOperationException();
            }
        }


        Log.LOGGER.info("Finished. stack: "+opstack);
    }

}
