package outskirts.lang.langdev.symtab;

public class SymbolClass extends Symbol implements TypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public SymbolClass(String name, Symtab parent) {
        super(name, parent);
    }

    @Override
    public String getName() {
        return parNam();
    }
}
