package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class CodeBuf {

    public static final byte
            NULL = 0,
            STORE = 1,
            LDC = 2,
            INVOKEFUNC = 3;

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

    public void _store(String name) {
        _store(findvar(name));
    }
    public void _store(int i) {
        append(STORE);
        append((byte)i);
    }

    public void _ldc(short cpidx) {
        append(LDC);
        append((byte)((cpidx >>> 8) & 0xFF));
        append((byte)(cpidx & 0xFF));
    }

    // funcptr, args...
    public void _invokefunc() {
        append(INVOKEFUNC);
    }

    void append(byte b) {
        buf.add(b);
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
