package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.compiler.ClassFile;

import java.util.*;

public class SymbolClass extends BaseSymbol implements ScopedSymbol, TypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

//    public ClassFile compiledclfile;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

    public static List<SymbolClass> genericsFilledClasses = new ArrayList<>();

    public AST_Stmt_DefClass genericsPrototypeClassAST;

    public static SymbolClass lookupOrBuildFullfilledTemplate(SymbolClass prototype, List<TypeSymbol> gtype_args, ASTSymolize symize) {

        Scope originalscope = prototype.getSymbolTable().getParent();
        SymbolClass r = (SymbolClass)originalscope.findLocalSymbol(prototype.getSimpleName()+ASTSymolize._GenericsArgumentsString(gtype_args));
        if (r != null)
            return r;

        // resolve.
        AST_Stmt_DefClass a = prototype.genericsPrototypeClassAST;
        a.genericsTmpFullfilledArguments = gtype_args;
        symize.visitStmtDefClass(a, originalscope);
        r = a.genericsTmpFullfilledSymbol;

        if (!genericsFilledClasses.contains(r))
            genericsFilledClasses.add(r);

        return r;
    }

//    public Symbol getInstanceSymbol() {  // really.?
//        return ;
//    }

    @Override
    public Scope getSymbolTable() {
        return symtab;
    }

    private static final int OBJECT_HEADER_SIZE = 0;

    public int memoffset(String flname) {
        int size = OBJECT_HEADER_SIZE;
        for (Symbol s : getSymbolTable().getMemberSymbols()) {
            if (s instanceof SymbolVariable) {
                if (s.getSimpleName().equals(flname))
                    return size;
                size += ((SymbolVariable)s).type.getTypesize();
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public int getTypesize() {
        int size = OBJECT_HEADER_SIZE;
        for (Symbol s : getSymbolTable().getMemberSymbols()) {
            if (s instanceof SymbolVariable) {
                SymbolVariable c = (SymbolVariable)s;
                size += c.getType().getTypesize();
            }
        }
        return size;
    }

}
