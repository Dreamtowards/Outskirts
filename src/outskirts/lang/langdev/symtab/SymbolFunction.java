package outskirts.lang.langdev.symtab;

// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends SymbolClass {

    public final TypeSymbol returntype;

    public SymbolFunction(String name, TypeSymbol returntype) {
        super(name, null);
        this.returntype = returntype;
    }
}
