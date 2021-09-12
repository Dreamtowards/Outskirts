package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.*;
import outskirts.util.StringUtils;

import java.util.*;

// Scope.
public final class Scope {

    /**
     * Enclosing/Outer 'Scope'.
     */
    private final Scope enclosing;

    // lookup by stringName? how about diff type..
    /**
     * note that the String-Key-Map is Important, Finding Symbol by Map-Key instead of Actual-Symbol-Name.
     * the Key-String might not Actual-Symbol-Name, but Searching-Name. (alias name.)
     *
     * because the functionality of "typealias (using .. as ..)",
     * the Symbol is Unique, Symbol.name is its Actually-Name. see getQualifiedName(): we won't give a alias name as result, because its leads wrong address,
     * we won't duplicate the Symbol for the typealias functionality, Symbol-Instance/Object just Unique, but we search it/local-using-it by custom name - just in the map's key.
     */
    public final Map<String, BaseSymbol> _symbols = new HashMap<>();

    // e.g. SymbolClass, SymbolNamespace.
    public BaseSymbol symbolAssociated;
    public AST astAssociated;

    public Scope(Scope enclosing) {
        this.enclosing = enclosing;
    }

    public Scope getParent() {
        return enclosing;
    }

    public final Collection<BaseSymbol> getMemberSymbols() {
        return Collections.unmodifiableCollection(_symbols.values());
    }

    public BaseSymbol findLocalSymbol(String name) {
        return _symbols.get(name);
    }
    private void internalDefineLocalSymbol(String name, BaseSymbol symbol) {
        if (_symbols.containsKey(name))
            throw new IllegalStateException("Symbol '"+name+"' already defined in this scope.");
        _symbols.put(name, symbol);
    }

    public void define(BaseSymbol symbol) {
        internalDefineLocalSymbol(symbol.getSimpleName(), symbol);
    }
    // for functionality of "typealias". (using .. as ..)
    public void defineAsCustomName(String asname, BaseSymbol symbol) {
        internalDefineLocalSymbol(asname, symbol);
    }

    public BaseSymbol resolve(String name) {
        BaseSymbol s = findLocalSymbol(name);
        if (s != null)
            return s;
        if (enclosing != null)
            return enclosing.resolve(name);

        throw new IllegalStateException("Couldn not resolve symbol \""+name+"\". on "+this);
    }


    // MemberAccess "base.fur.inr"
    public <T extends BaseSymbol> T resolveQualifiedName(String qualifiedname) {  // resolveMAStr
        BaseSymbol s = null;
        for (String nm : StringUtils.explode(qualifiedname, ".")) {
            if (s == null) {  // assert idx==0;
                s = resolve(nm);
            } else {
                s = ((ScopedSymbol)s).getSymbolTable().resolveMember(nm);
            }
        }
        return (T)s;
    }

    public <T extends BaseSymbol> T resolveQualifiedExpr(AST_Expr a) {  // resolveMAExpr
        if (a instanceof AST_Expr_MemberAccess) {
            AST_Expr_MemberAccess c = (AST_Expr_MemberAccess)a;
            ScopedSymbol l = resolveQualifiedExpr(c.getExpression());
            return (T)l.getSymbolTable().resolveMember(c.getIdentifier());
        } else {
            return (T)resolve(((AST_Expr_PrimaryIdentifier)a).getName());
        }
    }

    public BaseSymbol resolveMember(String name) {
        return Objects.requireNonNull(findLocalSymbol(name), "Could not resolve member-symbol '"+name+"'");
    }

    public SymbolFunction lookupEnclosingFuncction() {
        if (symbolAssociated instanceof SymbolFunction) {
            return (SymbolFunction)symbolAssociated;
        } else if (getParent() != null) {
            return getParent().lookupEnclosingFuncction();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return "Symtab{" +
                "parent=" + enclosing +
                ", symbols=" + _symbols +
                '}';
    }
}
