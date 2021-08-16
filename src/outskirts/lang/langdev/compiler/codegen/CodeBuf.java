package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static outskirts.lang.langdev.compiler.codegen.Opcodes.*;

public class CodeBuf {

    private final List<String> localvars = new ArrayList<>();
    private final List<Byte> buf = new ArrayList<>();

    public ConstantPool constantpool;

    public CodeBuf(ConstantPool constantpool) {
        this.constantpool = constantpool;
    }

    public int findvar(String name) {
        int i = localvars.indexOf(name);
        if (i == -1) throw new IllegalStateException("Not found variable "+name);
        return i;
    }
    public void defvar(String name) {
        Validate.isTrue(!localvars.contains(name), "Already def variable.");
        localvars.add(name);
    }
    public int localsize() {
        return localvars.size();
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
    public void _invokefunc() {
        append(INVOKEFUNC);
    }

    public void _jmpifn(int i) {
        append(JMP_F);
        appendShort((short)i);
    }
    public Consumer<Integer> _jmpifn_delay() {
        int mark = idx()+1;
        _jmpifn(0);
        return p -> {
            setShort(mark, (short)(int)p);
        };
    }
    public void _jmp(int i) {
        append(JMP);
        appendShort((short)i);
    }
    public Consumer<Integer> _jmp_delay() {
        int mark = idx()+1;
        _jmp(0);
        return p -> {
            setShort(mark, (short)(int)p);
        };
    }

    public void _i32add() {
        append(I32ADD);
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

    public void _dup() {
        append(DUP);
    }

    public void _pop() {
        append(POP);
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
        return "CodeBuf{" +
                "localvars=" + localvars +
                ", buf=" + buf +
                ", constantpool=" + constantpool +
                '}';
    }
}
