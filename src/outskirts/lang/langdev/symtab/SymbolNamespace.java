package outskirts.lang.langdev.symtab;

public class SymbolNamespace extends Symbol implements ScopedTypeSymbol {

    public Scope symtab;

    public SymbolNamespace(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

    @Override
    public Scope getTable() {
        return symtab;
    }

    @Override
    public int typesize() {
        throw new UnsupportedOperationException();
    }
}
