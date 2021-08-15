package outskirts.lang.langdev.symtab;

import java.util.Map;

// is that All Symbol has sub-symbols??

// anyone symbol has not Table.?
public abstract class Symbol extends Symtab {

    public String name;

    public Symbol(String name, Symtab parent) {
        super(parent);
        this.name = name;
    }

    public String parNam() {
        StringBuilder sb = new StringBuilder(name);
        Symtab s = this;
        while ((s=s.getParent()) != null) {
            if (s instanceof Symbol)
                sb.insert(0, ((Symbol)s).name+".");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+parNam()+"}";
    }
}
