package outskirts.lang.langdev.symtab;

public class SymbolNamespace extends Symbol implements TypeSymbol, ScopedTypeSymbol {

    public Scope symtab;

    public SymbolNamespace(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
        symtab.symbolAssociated = this;
    }

    @Override
    public String getQualifiedName() {
        return SymbolClass.composeFullName(symtab);
    }

    @Override
    public Scope getTable() {
        return symtab;
    }
}
