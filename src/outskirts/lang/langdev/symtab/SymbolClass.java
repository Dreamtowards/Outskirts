package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.util.CollectionUtils;

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
