package outskirts.lang.langdev.symtab;

public class SymbolBuiltinType extends Symbol implements TypeSymbol {

    public SymbolBuiltinType(String name) {
        super(name);
    }

    public static final SymbolBuiltinType
            _string = new SymbolBuiltinType("string"),
            _int = new SymbolBuiltinType("int"),
            _void = new SymbolBuiltinType("void"),
            _function = new SymbolBuiltinType("function");   // Generic.?

    public static void init(Scope glob) {
        glob.define(_string);
        glob.define(_int);
        glob.define(_void);
        glob.define(_function);
    }

    @Override
    public String getQualifiedName() {
        return name;
    }

}
