package outskirts.lang.langdev.symtab;

public class SymbolGenericsTypeParameter extends BaseSymbol {

    // ? doesn't know.  when Decl, not defined ActualType.
    // TypeSymbol type;

    public SymbolGenericsTypeParameter(String name) {
        super(name);
    }

//    public TypeSymbol getType() {
//        return type;
//    }
//    public void defineType(TypeSymbol type) {
//        this.type = type;
//    }

    @Override
    public String getQualifiedName() {
        return getSimpleName();//+"="+getType().getQualifiedName();
    }
}
