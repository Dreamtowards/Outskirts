package outskirts.lang.langdev.symtab;

import org.lwjgl.Sys;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Expr_TypeCast;
import outskirts.lang.langdev.ast.AST_Stmt_DefFunc;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// why SymbolFunction is a TypeSymbol..?

/**
 * Sometimes, people think while let a Function, as a class `function<ret_type, param_type..>`, then things will been more common,
 * like a FuncCall operator `..(args..)` can applies on 'Anywhere' that reproduced an object. insteadof only can calling a function with a fixed funcName.
 *
 *
 */
// this maybe temporary.  until Generic Available. vartype:: function<return_type, param_type...>

// why not is a ScopedSymbol?
public class SymbolFunction extends BaseSymbol implements ModifierSymbol, ScopedSymbol {

    private final TypeSymbol returntype;
    public final List<SymbolVariable> params;  // actual params. may include 'this'. order sensitive: codegen invokeproc args, 'this' is heading.
    public final SymbolClass ownerclass;

    private final Scope fnscope;

    public CodeBuf codebuf;

    private final short modifiercode;

    public AST_Stmt_DefFunc genericsTmpASTForCompile;

    public SymbolFunction(String fname, List<SymbolVariable> params, TypeSymbol returntype, SymbolClass ownerclass, short modifiercode, Scope fnscope) {
        super(fname);
        this.params = params;
        this.returntype = returntype;
        this.ownerclass = ownerclass;
        this.modifiercode = modifiercode;
        this.fnscope = fnscope;
    }

    public static String getParametersSignature(List<TypeSymbol> ts) {
        return CollectionUtils.toString(ts, ",", Symbol::getQualifiedName);
    }

    public TypeSymbol getReturnType() {
        return returntype;
    }

    public SymbolClass getOwnerClass() {
        return ownerclass;
    }

    // note that not aligned with AST_Expr_FuncCall.getArguments().
    public List<SymbolVariable> getParameters() {
        return params;
    }
    public List<TypeSymbol> getParameterTypes() {
        return params.stream().map(SymbolVariable::getType).collect(Collectors.toList());
    }
    // parameters without 'this'. used for semantic func-call args type-check.
    public List<SymbolVariable> getDeclaredParameters() {
        return isStatic() ? getParameters() : getParameters().subList(1, getParameters().size());
    }

    @Override
    public Scope getSymbolTable() {
        return fnscope;
    }

    @Override
    public String getQualifiedName() {
//        String[] prms = params.stream().map(p -> p.getType().getQualifiedName() + " "+p.getSimpleName()).toArray(String[]::new);
//        return ownerclass.getQualifiedName()+"."+getSimpleName()+"("+String.join(",", prms)+"):"+getReturnType().getQualifiedName();
        return getOwnerClass().getQualifiedName()+"::"+getSimpleName()+"("+getParametersSignature(getParameterTypes())+")";
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
        Validate.isTrue(!sf.getParameterTypes().equals(getParameterTypes()), "Failed overwrite function, same signature.");  // toString optim.

        if (nextOverwriteFunc == null) {
            nextOverwriteFunc = sf;
        } else {
            nextOverwriteFunc.defineOverwriteFunc(sf);
        }
    }

    public SymbolFunction findOverwrites_PolymorphismTrans(List<TypeSymbol> argts, List<AST_Expr> args_ToTrans) {
        List<TypeSymbol> prmts = getParameterTypes();
        if (prmts.size() == argts.size()) {
            // check Params/Args
            boolean pass = true;
            for (int i = 0;i < prmts.size();i++) {
                TypeSymbol prm = prmts.get(i);
                TypeSymbol arg = argts.get(i);
                // both parameter and argument are class-pointer type.
                if (prm instanceof SymbolBuiltinTypePointer prmptr && arg instanceof SymbolBuiltinTypePointer argptr &&
                    prmptr.getPointerType() instanceof SymbolClass prmcls && argptr.getPointerType() instanceof SymbolClass argcls) {
                    if (SymbolClass.isBaseTypeOf(prmcls, argcls)) {  // Compatable! Polymorphism
                        if (prmcls != argcls && args_ToTrans != null) {  // do the Polymorphism type-cast.
                            AST_Expr typ = new AST_Expr() {}; typ.setSymbol(prmptr);
                            AST_Expr_TypeCast acast = new AST_Expr_TypeCast(args_ToTrans.get(i), typ);

                            acast.setSymbol(prmptr.rvalue());  // TypeCast sym.
                            args_ToTrans.set(i, acast);
                        }
                    } else {  // Not compatable.
                        pass = false;
                        break;
                    }
                } else if (prm != arg) {
                    pass = false;
                    break;
                }
            }
            if (pass)
                return this;
        }

        if (nextOverwriteFunc == null)
            return null;
//            throw new NoSuchElementException("Not found overwrite func "+getSimpleName()+"("+getParametersSignature(argts)+"). self: ("+getParametersSignature(prmts)+")");
        else
            return nextOverwriteFunc.findOverwrites_PolymorphismTrans(argts, args_ToTrans);
    }
}
