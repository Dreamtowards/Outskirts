package outskirts.lang.langdev.symtab;

public class SymbolNamespace extends BaseSymbol implements ScopedSymbol {

    public Scope symtab;

    public SymbolNamespace(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

    @Override
    public Scope getSymbolTable() {
        return symtab;
    }

}
