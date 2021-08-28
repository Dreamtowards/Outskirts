package outskirts.lang.langdev.machine;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.Opcodes;
import outskirts.util.IOUtils;
import outskirts.util.Intptr;

import java.util.LinkedList;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;

public class Machine {

    public static byte[] MemSpace = new byte[1024 * 1024 * 4];

    public static void exec(CodeBuf codebuf) {
        byte[] code = codebuf.toByteArray();

        Intptr t = Intptr.zero();
        while (t.i < code.length) {
            System.out.println(Opcodes._InstructionComment(codebuf, t));
        }

        int pc = 0;
        LinkedList<Object> opstack = new LinkedList<>();
        Object[] locals = new Object[codebuf.localsize()];

        while (pc < code.length) {
//            System.out.println(" EXEC INST "+Opcodes._InstructionComment(code, Intptr.of(pc)));
            switch (code[pc++]) {
                case STORE: {
                    Object o = opstack.pop();
                    byte i = code[pc++];

                    locals[i] = o;
                    break;
                }
                case LOAD: {
                    byte i = code[pc++];
                    opstack.push(locals[i]);

                    break;
                }
                case LDC: {
                    short i = IOUtils.readShort(code, pc);
                    pc += 2;
                    ConstantPool.Constant c = codebuf.constantpool.get(i);

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
                case JMP_F: {
                    Object o = opstack.pop();
                    if ((int)o == 0) {
                        pc = IOUtils.readShort(code, pc);
                    } else {
                        pc += 2;
                    }
                    break;
                }
                case I32ADD: {
                    int i2 = (int)opstack.pop();
                    int i1 = (int)opstack.pop();
                    opstack.push(i1+i2);
                    break;
                }
                case DUP: {
                    opstack.push(opstack.peek());
                    break;
                }
                case ICMP: {
                    int i2 = (int)opstack.pop();
                    int i1 = (int)opstack.pop();
                    int r = i1 - i2;
                    int cmpr;
                    if (r < 0) cmpr = CMPR_LT;
                    else if (r > 0) cmpr = CMPR_GT;
                    else cmpr = CMPR_EQ;
                    opstack.push(cmpr);
                    break;
                }
                case CMP_LT: {
                    opstack.push((int)opstack.pop() == CMPR_LT ? 1 : 0);
                    break;
                }
                case CMP_GT: {
                    opstack.push((int)opstack.pop() == CMPR_GT ? 1 : 0);
                    break;
                }
                case CMP_EQ: {
                    opstack.push((int)opstack.pop() == CMPR_EQ ? 1 : 0);
                    break;
                }
                case POP: {
                    opstack.pop();
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal instruction. #"+pc);
            }
        }

        System.out.println("Done Exec. opstack: "+opstack);
    }

    public static final int CMPR_LT = 0,
                            CMPR_GT = 1,
                            CMPR_EQ = 2;
}
