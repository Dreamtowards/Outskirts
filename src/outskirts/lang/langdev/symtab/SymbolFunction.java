package outskirts.lang.langdev.symtab;

// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends SymbolClass {

    public final SymbolClass returntype;

    public SymbolFunction(String name, SymbolClass returntype) {
        super(name, null);
        this.returntype = returntype;
    }
}
