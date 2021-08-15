package outskirts.lang.langdev.symtab;

// Needs Scope? for Instance Member access.
public class SymbolVariable extends Symbol {

    public final SymbolClass classtype;

    public SymbolVariable(String name, Symtab parent, SymbolClass classtype) {
        super(name, parent);
        this.classtype = classtype;
    }
}
