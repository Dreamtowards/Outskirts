package outskirts.lang.langdev.symtab;

public class SymbolBuiltinType extends Symbol implements TypeSymbol {

    public SymbolBuiltinType(String name) {
        super(name);
    }

    public static final SymbolBuiltinType
            _int = new SymbolBuiltinType("int"),
            _void = new SymbolBuiltinType("void"),
            _ptr = new SymbolBuiltinType("ptr");

    public static void init(Scope glob) {
        glob.define(_int);
        glob.define(_void);
        glob.define(_ptr);
    }

    @Override
    public String getQualifiedName() {
        return name;
    }

    @Override
    public int typesize() {
        if (this == _int) return 4;
        if (this == _void) throw new IllegalStateException();
//        if (this == _ptr) return 4;

        throw new IllegalStateException(name);
    }

    @Override
    public String toString() {
        return "SymbolBuiltinType{"+name+"}";
    }
}
