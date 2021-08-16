package outskirts.lang.langdev.machine;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.util.IOUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static outskirts.lang.langdev.compiler.Opcodes.*;

public class Machine {

    public static void exec(byte[] code, ConstantPool cpool) {
        int pc = 0;
        LinkedList<Object> opstack = new LinkedList<>();
        List<Object> locals = new ArrayList<>();

        while (pc < code.length) {
            switch (code[pc++]) {
                case STORE: {
                    Object o = opstack.pop();
                    byte i = code[pc++];

                    locals.set(i, o);
                    break;
                }
                case LDC: {
                    short i = IOUtils.readShort(code, pc);
                    pc += 2;
                    ConstantPool.Constant c = cpool.get(i);

                    if (c instanceof ConstantPool.Constant.CUtf8) opstack.push(((ConstantPool.Constant.CUtf8) c).tx);
                    else if (c instanceof ConstantPool.Constant.CInt32) opstack.push(((ConstantPool.Constant.CInt32) c).i);
                    else throw new IllegalStateException("Unsupported Constant Type.");

                    System.out.println("Load Constant: "+opstack.peek());
                    break;
                }
                case INVOKEFUNC: {
                    throw new IllegalStateException();
                }
                case JMP: {
                    pc = IOUtils.readShort(code, pc);
                    break;
                }
                case JMP_IFN: {
                    Object o = opstack.pop();
                    if ((o instanceof Integer && (int)o == 0) ||
                        (o instanceof Float   && (float)o == 0f) ||
                         o == null) {
                        pc = IOUtils.readShort(code, pc);
                    }
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal instruction.");
            }
        }
    }

}
