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

    @Override
    public int typesize() {
        final int HEADER_SIZE = 8;
        int size = HEADER_SIZE;
        for (Symbol s : getTable().getMemberSymbols()) {
            int msz;
            if (s instanceof TypeSymbol) {
                msz = ((TypeSymbol)s).typesize();
            } else {
                SymbolVariable c = (SymbolVariable)s;
                msz = c.type.typesize();
            }
            size += msz;
        }
        return size;
    }
}
