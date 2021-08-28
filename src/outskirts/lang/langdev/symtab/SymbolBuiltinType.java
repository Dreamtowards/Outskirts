package outskirts.lang.langdev.symtab;

public class SymbolBuiltinType extends Symbol implements TypeSymbol {

    public SymbolBuiltinType(String name) {
        super(name);
    }

    public static final SymbolBuiltinType
            _int = new SymbolBuiltinType("int"),
            _void = new SymbolBuiltinType("void");   // Generic.?

    public static void init(Scope glob) {
        glob.define(_int);
        glob.define(_void);
    }

    @Override
    public String getQualifiedName() {
        return name;
    }

    @Override
    public int typesize() {
        if (this == _int) return 4;
        if (this == _void) throw new IllegalStateException();

        throw new IllegalStateException();
    }
}
