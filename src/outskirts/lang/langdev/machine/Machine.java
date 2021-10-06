package outskirts.lang.langdev.machine;

import org.lwjgl.Sys;
import outskirts.lang.langdev.Main;
import outskirts.lang.langdev.compiler.ClassCompiler;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.Opcodes;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.SymbolFunction;
import outskirts.lang.langdev.symtab.SymbolVariable;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Intptr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;
import static outskirts.util.IOUtils.readShort;

public class Machine {

    public static byte[] MemSpace = new byte[100];
    private static int esp = 0;

    private static int str_constant_ai_ptr = 62;
    private static final Map<String, Integer> str_constants = new HashMap<>();

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
    private static void memset(int dest, int len, int val) {
        for (int i = 0;i < len;i++) {
            MemSpace[dest+i] = (byte)val;
        }
    }

    private static void pushn(int destptr, int n) {
        memcpy(destptr, esp, n);
        esp += n;
    }
    private static void pushi32(int i) {
        IOUtils.writeInt(MemSpace, esp, i);
        esp += 4;
    }
    private static void pushi8(byte i) {
        MemSpace[esp] = i;
        esp += 1;
    }

    private static int popi32() {
        esp -= 4;
        return IOUtils.readInt(MemSpace, esp);
    }
    private static int pop_ptr() {
        return popi32();
    }
    private static int popi8() {
        esp -= 1;
        return MemSpace[esp];
    }
    private static void popn(int destptr, int n) {
        esp -= n;
        memcpy(esp, destptr, n);
    }

    public static void exec(CodeBuf buf) {
//        System.out.println(buf.toString());

        byte[] code = buf.toByteArray();

        int ip = 0;
//        final int baseptr = 8;  // rand.

//        int[] localsizes = new int[buf.numlocals()];  // locals type-size.
//        int tmpi = 0;
//        for (TypeSymbol typ : buf.localmap().values()) {
//            localsizes[tmpi++] = typ.typesize();
//        }
        final int begin_esp = esp;

        int[] localptrs = new int[buf.numlocals()];
        for (int i = 0;i < localptrs.length;i++) {
            localptrs[i] = esp;
            esp += buf.localsz(i);
        }

        final int rvp = esp;  // rvalue initptr. end of locals.

        while (ip < code.length) {

            byte[] lcspc = CollectionUtils.subarray(MemSpace, begin_esp, esp);

            System.out.printf("inst %-40s;  opstack: beg="+begin_esp+" rvp="+rvp+", esp="+esp+", locals: "+Arrays.toString(dump(lcspc))+"\n", Opcodes._InstructionComment(buf, code, Intptr.of(ip)));
            boolean doret = false;
            final byte INST = code[ip++];
            switch (INST) {
//                case STORE: {
//                    byte i = code[ip++];
//                    popn(localptrs[i], buf.localsz(i));
//                    break;
//                }
                case LLOADP: {
                    byte i = code[ip++];
                    pushi32(localptrs[i]);
                    break;
                }
                case LDC: {
                    short i = readShort(code, ip);
                    ip += 2;
                    ConstantPool.Constant c = buf.cp.get(i);

                    if (c instanceof ConstantPool.Constant.CInt32) {
                        int v = ((ConstantPool.Constant.CInt32)c).i;
                        pushi32(v);
                    } else if (c instanceof ConstantPool.Constant.CUtf8) {
                        String v = ((ConstantPool.Constant.CUtf8)c).tx;
                        Integer p = str_constants.get(v);
                        if (p == null) {
                            System.err.println("Load STR "+v+" base on "+str_constant_ai_ptr);
                            p = str_constant_ai_ptr;
                            for (int j = 0;j < v.length();j++) {
                                IOUtils.writeInt(MemSpace, str_constant_ai_ptr, v.charAt(j));
                                str_constant_ai_ptr += 4;
                            }
                            IOUtils.writeInt(MemSpace, str_constant_ai_ptr, 0);
                            str_constant_ai_ptr += 4;
                            str_constants.put(v, p);
                        }
                        pushi32(p);
                    } else
                        throw new UnsupportedOperationException();

//                    if (c instanceof ConstantPool.Constant.CUtf8) opstack.push(((ConstantPool.Constant.CUtf8) c).tx);
//                    else if (c instanceof ConstantPool.Constant.CInt32) opstack.push(((ConstantPool.Constant.CInt32) c).i);
//                    else throw new IllegalStateException("Unsupported Constant Type.");

//                    System.out.println("Load Constant: "+opstack.peek());
                    break;
                }
                case POPCPY: {
                    byte sz = code[ip++];

                    // maybe dest-ptr should after rvalue-data.
                    esp -= sz;
                    int srcp = esp;
                    int dstp = popi32();
                    memcpy(srcp, dstp, sz);
                    //System.out.println("PopCpy: "+srcp+"->"+dstp);

                    break;
                }
                case PTRCPY: {
                    byte sz = code[ip++];

                    int srcptr = popi32();
                    int dstptr = popi32();
                    memcpy(srcptr, dstptr, sz);
                    //System.out.println("PtrCpy: "+srcptr+"->"+dstptr);

                    break;
                }
                case LOADV: {
                    byte sz = code[ip++];

                    int p = popi32();
                    pushn(p, sz);

                    break;
                }
                case STKPTR_OFF: {
                    byte off = code[ip++];

                    pushi32(esp+off);

                    break;
                }
//                case LDPTR: {
//                    byte sz = code[ip++];
//
//                    int ptr = popi32();
//                    pushn(ptr, sz);
//
//                    break;
//                }
//                case STPTR: {
//                    byte sz = code[ip++];
//
//                    int ptr = popi32();
//                    popn(ptr, sz);
//
//                    break;
//                }
                case INVOKEFUNC: {
                    String sfname = buf.cp.getUTF8(readShort(code, ip)); ip+=2;

                    int begArg = sfname.indexOf('('),
                        begFld = sfname.lastIndexOf('.', begArg);
                    String clname = sfname.substring(0, begFld);         // class name.
                    String flname = sfname.substring(begFld+1, begArg);  // field name.


                    SymbolFunction sf = (SymbolFunction)resolveClass(clname).getSymbolTable().resolveMember(flname);

                    int params_sz = 0;
                    for (SymbolVariable sv : sf.params) {
                        params_sz += sv.getType().getTypesize();
                    }
                    esp -= params_sz;

                    System.out.println("INVOKEFUNC Load Function: "+sfname+", Clname: "+clname);
                    Machine.exec(sf.codebuf);

                    break;
                }
                case STACKALLOC: {
                    String clname = buf.cp.getUTF8(readShort(code, ip)); ip+=2;

                    SymbolClass cl = resolveClass(clname);
                    int sz = cl.getTypesize();
//                    System.out.println("SIZE"+sz);

                    memset(esp, sz, 0);
                    esp += sz;

                    break;
                }
                case RET: {
                    byte sz = code[ip++];

                    memcpy(esp-sz, begin_esp, sz);

                    esp = begin_esp + sz;
                    doret = true;

                    break;
                }
//                case GETFIELD: {
//                    String qualidname = buf.cp.getUTF8(readShort(code, ip)); ip+=2;
//
//                    int bord = qualidname.lastIndexOf('.');
//                    String clname = qualidname.substring(0, bord);
//                    String flname = qualidname.substring(bord+1);
//
//                    SymbolClass cl = resolveClass(clname);
//                    int clsz = cl.getTypesize();
//                    TypeSymbol fl_type = ((SymbolVariable)cl.getSymbolTable().resolveMember(flname)).type;
//                    int fl_typ_sz = fl_type.getTypesize();
//
//                    int beg_ = esp - clsz;
//                    memcpy(beg_+cl.memoffset(flname), beg_, fl_typ_sz);
//
//                    break;
//                }
//                case PUTFIELD: {
//                    String qualidname = buf.cp.getUTF8(readShort(code, ip)); ip+=2;
//
//                    int bord = qualidname.lastIndexOf('.');
//                    String clname = qualidname.substring(0, bord);
//                    String flname = qualidname.substring(bord+1);
//
//                    SymbolClass cl = resolveClass(clname);
//                    int clsz = cl.getTypesize();
//                    TypeSymbol fl_type = ((SymbolVariable)cl.getSymbolTable().resolveMember(flname)).type;
//                    int fl_typ_sz = fl_type.getTypesize();
//
//
//                    break;
//                }
                case JMP: {
                    ip = readShort(code, ip);
                    break;
                }
                case JMP_F: {
                    int b = popi8();
                    if (b == 0) {
                        ip = readShort(code, ip);
                    } else {
                        ip += 2;
                    }
                    break;
                }
                case I32ADD: {
                    int i2 = popi32();
                    int i1 = popi32();
                    pushi32(i1+i2);
                    break;
                }
                case I32MUL: {
                    int i2 = popi32();
                    int i1 = popi32();
                    pushi32(i1*i2);
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
                    int i2 = popi32();
                    int i1 = popi32();
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
                case CMP_NE: {
                    pushi8((byte)(popi8() != CMPR_EQ ? 1 : 0));
                    break;
                }
                case INC_I32:
                case DEC_I32: {
                    int p = pop_ptr();
                    int v = IOUtils.readInt(MemSpace, p);
                    IOUtils.writeInt(MemSpace, p, INST == INC_I32 ? v+1 : v-1);
                    pushi32(v);
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal instruction. #"+ip);
            }
            if (doret)
                break;
        }

        byte[] lcspc = CollectionUtils.subarray(MemSpace, begin_esp, rvp);
//        lcspc = CollectionUtils.subarray(MemSpace, 0, 32);

        System.out.println("Done Exec. opstack: beg="+begin_esp+" rvp="+rvp+", esp="+esp+", locals: "+Arrays.toString(dump(lcspc)));

    }

    public static final byte CMPR_LT = 0,
                             CMPR_GT = 1,
                             CMPR_EQ = 2;

    private static SymbolClass resolveClass(String clname) {
        return Main.glob.resolveQualifiedName(clname);
    }

    public static String[] dump(byte[] bytes) {
        String[] a = new String[bytes.length];
        for (int i = 0;i < bytes.length;i++) {
            byte b = bytes[i];
            a[i] = String.valueOf(b&0xff);
        }
        return a;
    }
}
