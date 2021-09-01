package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.compiler.codegen.CodeBuf;

// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends Symbol implements TypeSymbol {

    public final TypeSymbol returntype;
    public final SymbolClass ownerclass;

    public CodeBuf codebuf;

    public boolean isStaticFunction;

    public SymbolFunction(String name, TypeSymbol returntype, SymbolClass ownerclass) {
        super(name);
        this.returntype = returntype;
        this.ownerclass = ownerclass;
    }

    @Override
    public String getQualifiedName() {
        return "function<"+returntype.getQualifiedName()+", ..>";
    }

    @Override
    public int typesize() {
        return 8;  // ptrsize
    }
}
