package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.Intptr;
import outskirts.util.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;

public class CodeBuf {

    private final LinkedHashMap<String, TypeSymbol> localvars = new LinkedHashMap<>();
    private final List<Byte> buf = new ArrayList<>();

    public ConstantPool constantpool;

    public CodeBuf(ConstantPool constantpool) {
        this.constantpool = constantpool;
    }

    public int findvar(String name) {
        int i = 0;
        for (String s : localvars.keySet()) {
            if (s.equals(name))
                return i;
            i++;
        }
        throw new IllegalStateException("Not found variable "+name);
    }
    public void defvar(String name, TypeSymbol ts) {
        Validate.isTrue(!localvars.containsKey(name), "Already def variable '"+name+"'.");
        localvars.put(name, ts);
    }
    public int localsize() {
        return localvars.size();
    }
    public LinkedHashMap<String, TypeSymbol> localmap() {
        return localvars;
    }

    public int idx() {
        return buf.size();
    }

    public void _store(String name) {
        _store(findvar(name));
    }
    public void _store(int i) {
        append(STORE);
        append((byte)i);
    }

    public void _load(String name) {
        _load(findvar(name));
    }
    public void _load(int i) {
        append(LOAD);
        append((byte)i);
    }

    public void _ldc(short cpidx) {
        append(LDC);
        appendShort(cpidx);
    }

    // funcptr, args...
    public void _invokefunc(String sfname) {
        append(INVOKEFUNC);
        appendShort(constantpool.ensureUtf8(sfname));
    }

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

    public void _ldptr(int sz) {
        append(LDPTR);
        append((byte)sz);
    }

    public void _stptr(int sz) {
        append(STPTR);
        append((byte)sz);
    }

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

        sb.append("Constants:\n  ").append(constantpool).append("\n");
        sb.append("Locals:\n  ").append(localvars).append("\n");
        sb.append("Code:\n");
        Intptr t = Intptr.zero();
        while (t.i < buf.size()) {
            sb.append("  ").append(Opcodes._InstructionComment(this, t)).append("\n");
        }

        return sb.toString();
    }
}
