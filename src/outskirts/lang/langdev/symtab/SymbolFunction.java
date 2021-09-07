package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.compiler.codegen.CodeBuf;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sometimes, people think while let a Function, as a class `function<ret_type, param_type..>`, then things will been more common,
 * like a FuncCall operator `..(args..)` can applies on 'Anywhere' that reproduced an object. insteadof only can calling a function with a fixed funcName.
 *
 *
 */
// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends Symbol implements TypeSymbol {

    public final TypeSymbol returntype;
    public final List<TypeSymbol> params;
    public final SymbolClass ownerclass;

    public CodeBuf codebuf;

    public boolean isStaticFunction;

    public SymbolFunction(String name, List<TypeSymbol> params, TypeSymbol returntype, SymbolClass ownerclass) {
        super(name);
        this.params = params;
        this.returntype = returntype;
        this.ownerclass = ownerclass;
    }

    public TypeSymbol getReturnType() {
        return returntype;
    }

    @Override
    public String getQualifiedName() {
        return ownerclass.getQualifiedName()+"."+name+"("+String.join(",", params.stream().map(TypeSymbol::getQualifiedName).collect(Collectors.toList()))+"):"+returntype.getQualifiedName();
    }

    @Override
    public int typesize() {
        return 8;  // ptrsize
    }
}
