package outskirts.lang.langdev.machine;

import org.lwjgl.Sys;
import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.Opcodes;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Intptr;

import java.util.Arrays;
import java.util.LinkedList;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;

public class Machine {

    public static byte[] MemSpace = new byte[1024 * 1024 * 4];
    private static int esp;

    private static int sum(int[] a) {
        int s = 0;
        for (int i : a) {
            s += i;
        }
        return s;
    }

    private static void memcpy(int src, int dest, int len) {

        System.arraycopy(MemSpace, src, MemSpace, dest, len);
    }

    private static void pushn(int destptr, int n) {
        memcpy(destptr, esp, n);
        esp += n;
    }
    private static void pushi(int i) {
        IOUtils.writeInt(MemSpace, esp, i);
        esp += 4;
    }
    private static void pushi8(byte i) {
        MemSpace[esp] = i;
        esp += 1;
    }

    private static int popi() {
        esp -= 4;
        return IOUtils.readInt(MemSpace, esp);
    }
    private static int popi8() {
        esp -= 1;
        return MemSpace[esp];
    }

    private static void popn(int destptr, int n) {
        esp -= n;
        memcpy(esp, destptr, n);
    }

    public static void exec(CodeBuf codebuf) {
        byte[] code = codebuf.toByteArray();

        Intptr t = Intptr.zero();
        while (t.i < code.length) {
            System.out.println(Opcodes._InstructionComment(codebuf, t));
        }

        int pc = 0;
//        LinkedList<Object> opstack = new LinkedList<>();
//        Object[] locals = new Object[codebuf.localsize()];
        int baseptr = 8;  // rand.
        int[] localsizes = new int[codebuf.localsize()];  // locals type-size.
        int tmpi = 0;
        for (TypeSymbol typ : codebuf.localmap().values()) {
            localsizes[tmpi++] = typ.typesize();
        }

        int[] localptrs = new int[codebuf.localsize()];
        tmpi = baseptr;
        for (int i = 0;i < localptrs.length;i++) {
            localptrs[i] = tmpi;
            tmpi += localsizes[i];
        }
        esp = tmpi;  // rvalues baseptr.  baseptr+sum(localsizes)
        final int rvp = esp;

        while (pc < code.length) {
//            System.out.println(" EXEC INST "+Opcodes._InstructionComment(code, Intptr.of(pc)));
            switch (code[pc++]) {
                case STORE: {
                    byte i = code[pc++];
                    popn(localptrs[i], localsizes[i]);
                    break;
                }
                case LOAD: {
                    byte i = code[pc++];
                    pushn(localptrs[i], localsizes[i]);
                    break;
                }
                case LDC: {
                    short i = IOUtils.readShort(code, pc);
                    pc += 2;
                    ConstantPool.Constant c = codebuf.constantpool.get(i);

                    if (c instanceof ConstantPool.Constant.CInt32) {
                        int v = ((ConstantPool.Constant.CInt32)c).i;
                        pushi(v);
                    }

//                    if (c instanceof ConstantPool.Constant.CUtf8) opstack.push(((ConstantPool.Constant.CUtf8) c).tx);
//                    else if (c instanceof ConstantPool.Constant.CInt32) opstack.push(((ConstantPool.Constant.CInt32) c).i);
//                    else throw new IllegalStateException("Unsupported Constant Type.");

//                    System.out.println("Load Constant: "+opstack.peek());
                    break;
                }
                case LDPTR: {
                    byte sz = code[pc++];

                    int ptr = popi();
                    pushn(ptr, sz);
                    System.out.println("Ldptr["+ptr+"]: "+IOUtils.readInt(MemSpace, ptr));

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
                    int b = popi8();
                    if (b == 0) {
                        pc = IOUtils.readShort(code, pc);
                    } else {
                        pc += 2;
                    }
                    break;
                }
                case I32ADD: {
                    int i2 = popi();
                    int i1 = popi();
                    pushi(i1+i2);
                    break;
                }
                case I32MUL: {
                    int i2 = popi();
                    int i1 = popi();
                    pushi(i1*i2);
                    break;
                }
                case DUP: {
                    byte n = code[pc++];

                    memcpy(esp-n, esp, n);
                    esp += n;

                    break;
                }
                case POP: {
                    byte n = code[pc++];

                    esp -= n;

                    break;
                }
                case ICMP: {
                    int i2 = popi();
                    int i1 = popi();
                    int r = i1 - i2;
                    byte cmpr;
                    if (r < 0) cmpr = CMPR_LT;
                    else if (r > 0) cmpr = CMPR_GT;
                    else cmpr = CMPR_EQ;
                    pushi8(cmpr);
                    break;
                }
                case CMP_LT: {
                    pushi8((byte)(popi8() == CMPR_LT ? 1 : 0));
                    break;
                }
                case CMP_GT: {
                    pushi8((byte)(popi8() == CMPR_GT ? 1 : 0));
                    break;
                }
                case CMP_EQ: {
                    pushi8((byte)(popi8() == CMPR_EQ ? 1 : 0));
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal instruction. #"+pc);
            }
        }

        System.out.println("Done Exec. opstack: rvp="+rvp+", esp="+esp+", locals: "+ Arrays.toString(CollectionUtils.subarray(MemSpace, baseptr, rvp)));
    }

    public static final byte CMPR_LT = 0,
                             CMPR_GT = 1,
                             CMPR_EQ = 2;
}
