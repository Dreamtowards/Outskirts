package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.AST_Stmt_DefFunc;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.List;
import java.util.NoSuchElementException;

// why SymbolFunction is a TypeSymbol..?

/**
 * Sometimes, people think while let a Function, as a class `function<ret_type, param_type..>`, then things will been more common,
 * like a FuncCall operator `..(args..)` can applies on 'Anywhere' that reproduced an object. insteadof only can calling a function with a fixed funcName.
 *
 *
 */
// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>
public class SymbolFunction extends BaseSymbol implements ModifierSymbol {

    private final TypeSymbol returntype;
    public final List<SymbolVariable> params;  // actual params. may include 'this'. order sensitive: codegen invokeproc args, 'this' is heading.
    public final SymbolClass ownerclass;

    private final Scope fnscope;

    public CodeBuf codebuf;

    private final short modifiercode;

    public AST_Stmt_DefFunc genericsTmpASTForCompile;

//    public boolean isStaticFunction;  // todo: reduce by: Modifiers.isStatic(getModifierCode());

    public SymbolFunction(String fname, List<SymbolVariable> params, TypeSymbol returntype, SymbolClass ownerclass, short modifiercode, Scope fnscope) {
        super(fname);
        this.params = params;
        this.returntype = returntype;
        this.ownerclass = ownerclass;
        this.modifiercode = modifiercode;
        this.fnscope = fnscope;
    }

    public String getParametersSignature() {
        return CollectionUtils.toString(params, ",", e -> e.getType().getQualifiedName());
    }

    public TypeSymbol getReturnType() {
        return returntype;
    }

    public SymbolClass getOwnerSymbol() {
        return ownerclass;
    }

    // note that not aligned with AST_Expr_FuncCall.getArguments().
    public List<SymbolVariable> getParameters() {
        return params;
    }
    // parameters without 'this'. used for semantic func-call args type-check.
    public List<SymbolVariable> getDeclaredParameters() {
        return isStatic() ? getParameters() : getParameters().subList(1, getParameters().size());
    }

    @Override
    public String getQualifiedName() {
//        String[] prms = params.stream().map(p -> p.getType().getQualifiedName() + " "+p.getSimpleName()).toArray(String[]::new);
//        return ownerclass.getQualifiedName()+"."+getSimpleName()+"("+String.join(",", prms)+"):"+getReturnType().getQualifiedName();
        return getOwnerSymbol().getQualifiedName()+"::"+getSimpleName()+"("+getParametersSignature()+")";
    }

//    public SymbolVariable findVariable(Scope s, String name) {
//        Scope s = fnscope;
//        Symbol v;
//        while ((v=s.findLocalSymbol(name)) == null)
//    }

    @Override
    public short getModifierCode() {
        return modifiercode;
    }


    private SymbolFunction nextOverwriteFunc;

    public void defineOverwriteFunc(SymbolFunction sf) {
        Validate.isTrue(sf.getSimpleName().equals(getSimpleName()));
        Validate.isTrue(!sf.getParametersSignature().equals(getParametersSignature()));  // toString optim.

        if (nextOverwriteFunc == null) {
            nextOverwriteFunc = sf;
        } else {
            nextOverwriteFunc.defineOverwriteFunc(sf);
        }
    }
    public SymbolFunction findOverwriteFunc(String ps) {
        if (getParametersSignature().equals(ps))
            return this;

        if (nextOverwriteFunc == null)
            throw new NoSuchElementException("Not found overwrite func "+getSimpleName()+"("+ps+"). self: "+getParametersSignature());
        else
            return nextOverwriteFunc.findOverwriteFunc(ps);
    }
}
