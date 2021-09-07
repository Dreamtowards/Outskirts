package outskirts.lang.langdev.machine;

import outskirts.lang.langdev.compiler.ClassCompiler;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

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

        System.out.println(codebuf.toString());

        int ip = 0;
        final int baseptr = 8;  // rand.

//        LinkedList<Object> opstack = new LinkedList<>();
//        Object[] locals = new Object[codebuf.localsize()];


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
        final int rvp = esp;  // rvalue initptr. end of locals.

        while (ip < code.length) {
            switch (code[ip++]) {
                case STORE: {
                    byte i = code[ip++];
                    popn(localptrs[i], localsizes[i]);
                    break;
                }
                case LOAD: {
                    byte i = code[ip++];
                    pushn(localptrs[i], localsizes[i]);
                    break;
                }
                case LDC: {
                    short i = IOUtils.readShort(code, ip);
                    ip += 2;
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
                    byte sz = code[ip++];

                    int ptr = popi();
                    pushn(ptr, sz);

                    break;
                }
                case STPTR: {
                    byte sz = code[ip++];

                    int ptr = popi();
                    popn(ptr, sz);

                    break;
                }
                case INVOKEFUNC: {
                    short sfnameIdx = IOUtils.readShort(code, ip); ip+=2;
                    String sfname = ((ConstantPool.Constant.CUtf8)codebuf.constantpool.get(sfnameIdx)).tx;

                    int begArg = sfname.indexOf('('),
                        begFld = sfname.lastIndexOf('.', begArg);
                    String clname = sfname.substring(0, begFld);         // class name.
                    String flname = sfname.substring(begFld+1, begArg);  // field name.

                    System.out.println("Load Function: "+sfname+", Clname: "+clname);

                    ClassFile cf = findOrInitClass(clname);
                    ClassFile.Field fld = cf.findField(flname);

                    Machine.exec(fld._codebuf);

                    break;
                }
                case JMP: {
                    ip = IOUtils.readShort(code, ip);
                    break;
                }
                case JMP_F: {
                    int b = popi8();
                    if (b == 0) {
                        ip = IOUtils.readShort(code, ip);
                    } else {
                        ip += 2;
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
                    byte n = code[ip++];

                    memcpy(esp-n, esp, n);
                    esp += n;

                    break;
                }
                case POP: {
                    byte n = code[ip++];

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
                    throw new IllegalStateException("Illegal instruction. #"+ip);
            }
        }

        System.out.println("Done Exec. opstack: rvp="+rvp+", esp="+esp+", locals: "+ Arrays.toString(CollectionUtils.subarray(MemSpace, baseptr, rvp)));
    }

    public static final byte CMPR_LT = 0,
                             CMPR_GT = 1,
                             CMPR_EQ = 2;

    // clname: e.g. like "stl.lang.string"
    public static ClassFile findOrInitClass(String clname) {
        return Objects.requireNonNull(
                CollectionUtils.find(ClassCompiler._COMPILED_CLASSES, e -> e.thisclass.equals(clname)));
    }
}
