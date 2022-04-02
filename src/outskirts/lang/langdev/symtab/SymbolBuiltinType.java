package outskirts.lang.langdev.symtab;

public class SymbolBuiltinType extends BaseSymbol implements TypeSymbol {

    public SymbolBuiltinType(String name) {
        super(name);
    }

    public static final SymbolBuiltinType
            _i32 = new SymbolBuiltinType("i32"),
            _i8 = new SymbolBuiltinType("i8"),
            _void = new SymbolBuiltinType("void"),
            _bool = new SymbolBuiltinType("bool");

    public static void init(Scope glob) {
        glob.define(_i32);
        glob.define(_i8);
        glob.define(_void);
        glob.define(_bool);
    }

    @Override
    public String getQualifiedName() {
        return getSimpleName();
    }

    @Override
    public int getTypesize() {
        if (this == _i32) return 4;
        if (this == _i8) return 1;
        if (this == _void) return 0;
        if (this == _bool) return 1;

        throw new IllegalStateException(getSimpleName());
    }

    @Override
    public String toString() {
        return "SymbolBuiltinType{"+getSimpleName()+"}";
    }
}
