package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static outskirts.lang.langdev.compiler.Opcodes.*;

public class CodeBuf {

    private final List<String> localvars = new ArrayList<>();
    private final List<Byte> buf = new ArrayList<>();

    public ConstantPool constantpool;

    public CodeBuf(ConstantPool constantpool) {
        this.constantpool = constantpool;
    }

    public int findvar(String name) {
        int i = localvars.indexOf(name);
        if (i == -1) throw new IllegalStateException("Not found variable");
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
        append(JMP_IFN);
        appendShort((short)i);
    }
    public Consumer<Integer> _jmpifn_delay() {
        int mark = idx()+1;
        _jmpifn(0);
        return p -> {
            set(mark, (byte)((p >>> 8) & 0xFF));
            set(mark, (byte)( p        & 0xFF));
        };
    }
    public void _jmp(int i) {
        append(JMP);
        appendShort((short)i);
    }

    public void _i32add() {
        append(I32ADD);
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
