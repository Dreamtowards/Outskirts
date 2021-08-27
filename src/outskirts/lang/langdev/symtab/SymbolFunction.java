package outskirts.lang.langdev.symtab;

// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends Symbol implements TypeSymbol {

    public final TypeSymbol returntype;

    public SymbolFunction(String name, TypeSymbol returntype) {
        super(name);
        this.returntype = returntype;
    }

    @Override
    public String getQualifiedName() {
        return "function<"+returntype.getQualifiedName()+", ..>";
    }
}
