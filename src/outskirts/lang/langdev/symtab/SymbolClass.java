package outskirts.lang.langdev.symtab;

public class SymbolClass extends Symbol implements TypeSymbol, ScopedTypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
        symtab.symbolAssociated = this;
    }

    @Override
    public String getQualifiedName() {
        return composeFullName(symtab);
    }

    public static String composeFullName(Scope _tabl) {
        Scope tab = _tabl;
        StringBuilder sb = new StringBuilder(tab.symbolAssociated.name);
        while ((tab=tab.getParent()) != null) {
            if (tab.symbolAssociated != null) {
                sb.insert(0, tab.symbolAssociated.name+".");
            }
        }
        return sb.toString();
    }

    @Override
    public Scope getTable() {
        return symtab;
    }
}
