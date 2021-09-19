package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.Intptr;
import outskirts.util.Pair;
import outskirts.util.Validate;

import java.util.*;
import java.util.function.IntConsumer;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;

public class CodeBuf {

    private final List<Pair<String, TypeSymbol>> localvars = new ArrayList<>();

    private final List<Byte> buf = new ArrayList<>();

    public ConstantPool cp;

    public CodeBuf(ConstantPool constantpool) {
        this.cp = constantpool;
    }



    private int internalLocalidx(String name) {
        for (int i = 0;i < localvars.size();i++) {
            if (localvars.get(i).first.equals(name))
                return i;
        }
        return -1;
    }
    public int localidx(String name) {
        int i = internalLocalidx(name);
        if (i == -1)
            throw new IllegalStateException("Not found variable "+name);
        return i;
    }

    public void localdef(String name, TypeSymbol ts) {
        Validate.isTrue(internalLocalidx(name) == -1, "Already def variable '"+name+"'.");
        localvars.add(new Pair<>(name, ts));
    }
    public TypeSymbol localat(int i) {
        return localvars.get(i).second;
    }
    public int localsz(int i) {
        return localat(i).getTypesize();
    }
    public int numlocals() {
        return localvars.size();
    }

    public int idx() {
        return buf.size();
    }

//    public void _lstorep(String name) {
//        _lstorep(localidx(name));
//    }
//    public void _lstorep(int i) {
//        append(LSTOREP);
//        append((byte)i);
//    }
//
//    public void _lstorev(String name) {
//        _lstorev(localidx(name));
//    }
//    public void _lstorev(int i) {
//        append(LSTOREV);
//        append((byte)i);
//    }

    public void _lloadp(String name) {
        _lloadp(localidx(name));
    }
    public void _lloadp(int i) {
        append(LLOADP);
        append((byte)i);
    }

    public void _popcpy(int sz) {
        append(POPCPY);
        append((byte)sz);
    }
    public void _ptrcpy(int sz) {
        append(PTRCPY);
        append((byte)sz);
    }

    // popptr(), cpy(ptr, esp, n); esp += n;
    public void _loadv(int sz) {
        append(LOADV);
        append((byte)sz);
    }

    public void _stkptroff(int off) {
        append(STKPTR_OFF);
        append((byte)off);
    }

    public void _ldc(short cpidx) {
        append(LDC);
        appendShort(cpidx);
    }

    public void _ldv_i(int v) {
        _ldc(cp.ensureInt32(v));
    }

    // funcptr, args...
    public void _invokefunc(String sfname) {
        append(INVOKEFUNC);
        appendShort(cp.ensureUtf8(sfname));
    }

    public void _stackalloc(String clname) {
        append(STACKALLOC);
        appendShort(cp.ensureUtf8(clname));
    }

//    public void _getfield(String flname) {
//        append(GETFIELD);
//        appendShort(cp.ensureUtf8(flname));
//    }
//
//    public void _putfield(String flname) {
//        append(PUTFIELD);
//        appendShort(cp.ensureUtf8(flname));
//    }

    public void _jmpifn(int i) {
        append(JMP_F);
        appendShort((short)i);
    }
    public IntConsumer _jmpifn_delay() {
        int mark = idx()+1;
        _jmpifn(0);
        return p -> {
            setShort(mark, (short)p);
        };
    }
    public void _jmp(int i) {
        append(JMP);
        appendShort((short)i);
    }
    public IntConsumer _jmp_delay() {
        int mark = idx()+1;
        _jmp(0);
        return p -> {
            setShort(mark, (short)p);
        };
    }

    public void _i32add() {
        append(I32ADD);
    }
    public void _i32mul() {
        append(I32MUL);
    }

    public void _icmp() {
        append(ICMP);
    }
    public void _cmplt() {
        append(CMP_LT);
    }
    public void _cmpeq() {
        append(CMP_EQ);
    }
    public void _cmpgt() {
        append(CMP_GT);
    }

    public void _dup(int n) {
        append(DUP);
        append((byte)n);
    }

    public void _pop(int n) {
        append(POP);
        append((byte)n);
    }

//    public void _ldptr(int sz) {
//        append(LDPTR);
//        append((byte)sz);
//    }
//
//    public void _stptr(int sz) {
//        append(STPTR);
//        append((byte)sz);
//    }

    void append(byte b) {
        buf.add(b);
    }
    void appendShort(short s) {
        append((byte)((s >>> 8) & 0xFF));
        append((byte)( s        & 0xFF));
    }

    void set(int i, byte b) {
        buf.set(i, b);
    }
    void setShort(int i, short s) {
        set(i,   (byte)((s >>> 8) & 0xFF));
        set(i+1, (byte)( s        & 0xFF));
    }

    public byte[] toByteArray() {
        return CollectionUtils.toArrayb(buf);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Constants:\n  ").append(cp).append("\n");
        sb.append("Locals:\n  ").append(localvars).append("\n");
        sb.append("Code:\n");
        byte[] code = toByteArray();  // COST
        Validate.isTrue(code.length == buf.size());
        Intptr t = Intptr.zero();
        while (t.i < code.length) {
            sb.append("  ").append(Opcodes._InstructionComment(this, code, t)).append("\n");
        }

        return sb.toString();
    }
}
