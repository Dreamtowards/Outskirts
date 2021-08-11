package outskirts.lang.langdev.compiler;

import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class CodeBuf {

    public static final byte
            NULL = 0,
            STORE = 1,
            LDC = 2,
            INVOKEFUNC = 3;

    List<String> localvars = new ArrayList<>();
    List<Byte> buf = new ArrayList<>();

    ConstantPool constantpool;

    public CodeBuf(ConstantPool constantpool) {
        this.constantpool = constantpool;
    }

    int findvar(String name) {
        int i = localvars.indexOf(name);
        if (i == -1) throw new IllegalStateException("Not found variable");
        return i;
    }
    void defvar(String name) {
        Validate.isTrue(!localvars.contains(name), "Already def variable.");
        localvars.add(name);
    }

    void _store(String name) {
        _store(findvar(name));
    }
    void _store(int i) {
        append(STORE);
        append((byte)i);
    }

    void _ldc(short cpidx) {
        append(LDC);
        append((byte)((cpidx >>> 8) & 0xFF));
        append((byte)(cpidx & 0xFF));
    }

    // funcptr, args...
    void _invokefunc() {
        append(INVOKEFUNC);
    }

    void append(byte b) {
        buf.add(b);
    }
}
