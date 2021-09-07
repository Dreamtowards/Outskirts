package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.compiler.ClassFile;

import java.util.NoSuchElementException;

public class SymbolClass extends Symbol implements ScopedTypeSymbol {

    // public boolean isUsing = false;// nono, this will effect original DefClass.
//    public SymbolVariable standardInstanced = new SymbolVariable("<any:this>", this, this);

    public Scope symtab;

    public ClassFile compiledclfile;

    public SymbolClass(String name, Scope symtab) {
        super(name);
        this.symtab = symtab;
    }

    @Override
    public Scope getTable() {
        return symtab;
    }

    private static final int OBJECT_HEADER_SIZE = 8;

    public int memoffset(String flname) {
        int size = OBJECT_HEADER_SIZE;
        for (Symbol s : getTable().getMemberSymbols()) {
            if (s instanceof SymbolVariable) {
                if (s.name.equals(flname))
                    return size;
                size += ((SymbolVariable)s).type.typesize();
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public int typesize() {
        int size = OBJECT_HEADER_SIZE;
        for (Symbol s : getTable().getMemberSymbols()) {
            if (s instanceof SymbolVariable) {
                SymbolVariable c = (SymbolVariable)s;
                size += c.type.typesize();
            }
        }
        return size;
    }
}
