package outskirts.lang.langdev.symtab;

public class SymbolClass extends Symbol implements ScopedTypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
        symtab.symbolAssociated = this;
    }

    @Override
    public Scope getTable() {
        return symtab;
    }
}
