package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Expr_OperBi;
import outskirts.lang.langdev.ast.AST_Expr_PrimaryIdentifier;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

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
     *
     * because the functionality of "typealias (using .. as ..)",
     * the Symbol is Unique, Symbol.name is its Actually-Name. see getQualifiedName(): we won't give a alias name as result, because its leads wrong address,
     * we won't duplicate the Symbol for the typealias functionality, Symbol-Instance/Object just Unique, but we search it/local-using-it by custom name - just in the map's key.
     */
    public final Map<String, Symbol> _symbols = new HashMap<>();

    // e.g. SymbolClass, SymbolNamespace.
    public Symbol symbolAssociated;
    public AST astAssociated;

    public Scope(Scope enclosing) {
        this.enclosing = enclosing;
    }

    public Scope getParent() {
        return enclosing;
    }

    private Symbol findLocalSymbol(String name) {
        return _symbols.get(name);
    }
    private void internalDefineLocalSymbol(String name, Symbol symbol) {
        if (_symbols.containsKey(name))
            throw new IllegalStateException("Symbol '"+name+"' already defined in this scope.");
        _symbols.put(name, symbol);
    }

    public void define(Symbol symbol) {
        internalDefineLocalSymbol(symbol.name, symbol);
    }
    // for functionality of "typealias". (using .. as ..)
    public void defineAsCustomName(String asname, Symbol symbol) {
        internalDefineLocalSymbol(asname, symbol);
    }

    public Symbol resolve(String name) {
        Symbol s = findLocalSymbol(name);
        if (s != null)
            return s;
        if (enclosing != null)
            return enclosing.resolve(name);

        throw new IllegalStateException("Couldn not resolve symbol \""+name+"\". on "+this);
    }


    // MemberAccess "base.fur.inr"
    public <T extends Symbol> T resolveQualifiedName(String qualifiedname) {  // resolveMAStr
        Symbol s = null;
        for (String nm : StringUtils.explode(qualifiedname, ".")) {
            if (s == null) {  // assert idx==0;
                s = resolve(nm);
            } else {
                s = ((ScopedTypeSymbol)s).getTable().resolveMember(nm);
            }
        }
        return (T)s;
    }

    public <T extends Symbol> T resolveQualifiedExpr(AST_Expr a) {  // resolveMAExpr
        if (a instanceof AST_Expr_PrimaryIdentifier) {
            return (T)this.resolve(a.varname());
        } else if (a instanceof AST_Expr_OperBi) {
            AST_Expr_OperBi c = (AST_Expr_OperBi)a; Validate.isTrue(c.operator.equals("."));
            Symbol l = this.resolveQualifiedExpr(c.left);
            return (T)((ScopedTypeSymbol)l).getTable().resolveMember(c.right.varname());
        } else
            throw new IllegalStateException(a.toString());
    }

    public Symbol resolveMember(String name) {
        return Objects.requireNonNull(findLocalSymbol(name), "Could not resolve member-symbol '"+name+"'");
    }

    @Override
    public String toString() {
        return "Symtab{" +
                "parent=" + enclosing +
                ", symbols=" + _symbols +
                '}';
    }
}
