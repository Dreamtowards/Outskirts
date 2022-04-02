package outskirts.lang.langdev.symtab;

import org.lwjgl.Sys;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.util.CollectionUtils;

import java.util.*;

public class SymbolClass extends BaseSymbol implements ScopedSymbol, TypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

    private List<SymbolClass> superClasses = new ArrayList<>();
    public List<SymbolClass> getSuperClasses() {
        return superClasses;
    }

//    public ClassFile compiledclfile;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

//    public static List<SymbolClass> genericsFilledClasses = new ArrayList<>();

    public AST_Stmt_DefClass theGenericsPrototypeAST;

    private SymbolClass nextInstancedGenericsClass;  // linked. same prototype, diff generics instance.
    private List<TypeSymbol> theGenericsArguments;

    private String getGenericsSignature() {
        return CollectionUtils.toString(theGenericsArguments, ",", Symbol::getQualifiedName);
    }

    public SymbolClass lookupInstancedGenerics(List<TypeSymbol> sGenericsArguments, ASTSymolize visitor) {
        if (nextInstancedGenericsClass != null) {
            if (nextInstancedGenericsClass.getGenericsSignature().equals(getGenericsSignature())) {  // matched.
                return nextInstancedGenericsClass;
            } else {
                return nextInstancedGenericsClass.lookupInstancedGenerics(sGenericsArguments, visitor);  // passing.
            }
        } else {  // null, build.
            theGenericsPrototypeAST.tmpGenericsArguments = sGenericsArguments;

            Scope enclosingScope = getSymbolTable().getParent();
            visitor.visitStmtDefClass(theGenericsPrototypeAST, enclosingScope);

            nextInstancedGenericsClass = theGenericsPrototypeAST.tmpGenericsInstance;
            nextInstancedGenericsClass.theGenericsArguments = sGenericsArguments;
            return nextInstancedGenericsClass;
        }
    }

//    public Symbol getInstanceSymbol() {  // really.?
//        return ;
//    }

    @Override
    public Scope getSymbolTable() {
        return symtab;
    }

    public int calcFieldOffset(String flname) {
        int size = 0;
        for (Symbol s : getSymbolTable().getLocalSymbols()) {
            if (s instanceof SymbolVariable) {
                if (s.getSimpleName().equals(flname))
                    return size;
                size += ((SymbolVariable)s).getType().getTypesize();
            }
        }
        for (SymbolClass cl : superClasses) {
            try {
                return size+cl.calcFieldOffset(flname);
            } catch (Exception ex) { }  // bad example.
        }
        throw new NoSuchElementException("Not found field: "+flname);
    }

    public int calcBaseTypeOffset(SymbolClass sup) {
        if (sup == this)
            return 0;
        int size = getVariablesSize();
        for (SymbolClass cl : superClasses) {
            int i = cl.calcBaseTypeOffset(sup);
            if (i != -1)
                return size+i;
            size += cl.getTypesize();
        }
        return -1;
    }
    public static boolean isBaseTypeOf(SymbolClass base, SymbolClass sub) {
        return sub.calcBaseTypeOffset(base) != -1;
    }

    private int getVariablesSize() {
        int size = 0;
        for (Symbol s : getSymbolTable().getLocalSymbols()) {
            if (s instanceof SymbolVariable) { // requires !isStatic()?
                size += ((SymbolVariable)s).getType().getTypesize();
            }
        }
        return size;
    }

    @Override
    public int getTypesize() {
        int size = getVariablesSize();
        for (SymbolClass cl : superClasses) {
            size += cl.getTypesize();
        }
        return size;
    }

}
