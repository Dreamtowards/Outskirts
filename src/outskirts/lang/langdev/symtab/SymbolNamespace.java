package outskirts.lang.langdev.symtab;

public class SymbolNamespace extends Symbol implements ScopedTypeSymbol {

    public Scope symtab;

    public SymbolNamespace(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
        symtab.symbolAssociated = this;
    }

    @Override
    public Scope getTable() {
        return symtab;
    }
}
