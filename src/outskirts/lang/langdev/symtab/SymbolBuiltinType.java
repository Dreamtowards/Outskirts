package outskirts.lang.langdev.symtab;

public class SymbolBuiltinType extends BaseSymbol implements TypeSymbol {

    public SymbolBuiltinType(String name) {
        super(name);
    }

    public static final SymbolBuiltinType
            _int = new SymbolBuiltinType("int"),
            _void = new SymbolBuiltinType("void"),
//            _ptr = new SymbolBuiltinType("ptr"),
            _bool = new SymbolBuiltinType("bool");

    public static void init(Scope glob) {
        glob.define(_int);
        glob.define(_void);
//        glob.define(_ptr);
        glob.define(_bool);
    }

    @Override
    public String getQualifiedName() {
        return getSimpleName();
    }

    @Override
    public int getTypesize() {
        if (this == _int) return 4;
        if (this == _void) throw new IllegalStateException();
//        if (this == _ptr) return 4;

        throw new IllegalStateException(getSimpleName());
    }

    @Override
    public String toString() {
        return "SymbolBuiltinType{"+getSimpleName()+"}";
    }
}
