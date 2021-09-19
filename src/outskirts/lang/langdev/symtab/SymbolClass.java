package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.compiler.ClassFile;

import java.util.NoSuchElementException;

public class SymbolClass extends BaseSymbol implements ScopedSymbol, TypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

    public ClassFile compiledclfile;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

//    public Symbol getInstanceSymbol() {  // really.?
//        return ;
//    }

    @Override
    public Scope getSymbolTable() {
        return symtab;
    }

    private static final int OBJECT_HEADER_SIZE = 4;

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
