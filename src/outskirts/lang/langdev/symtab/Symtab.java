package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Expr_OperBi;
import outskirts.lang.langdev.ast.AST_Expr_PrimaryVariableName;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.*;

// Scope.
public class Symtab {

    private final Symtab parent;

    // lookup by stringName? how about diff type..
    public final List<Symbol> symbols = new ArrayList<>();

    public Symtab(Symtab parent) {
        this.parent = parent;
    }

    public Symtab getParent() {
        return parent;
    }

    private Symbol getSymbol(String name) {
        for (Symbol s : symbols) {
            if (s.name.equals(name))
                return s;
        }
        return null;
    }

    public void define(Symbol symbol) {
        Validate.isTrue(getSymbol(symbol.name) == null, "Already defined in the scope: "+symbol);

        symbols.add(symbol);
    }

    public Symbol resolve(String name) {
        Symbol s = getSymbol(name);
        if (s != null)
            return s;
        if (parent != null)
            return parent.resolve(name);

        throw new IllegalStateException("Couldn not resolve symbol \""+name+"\". on "+this);
    }


    // MemberAccess "base.fur.inr"
    public <T extends Symbol> T resolveMAStr(String s) {
        Symbol sym = null;
        for (String nm : StringUtils.explode(s, ".")) {
            if (sym == null)  // assert idx==0;
                sym = resolve(nm);
            else
                sym = sym.resolveMember(nm);
        }
        return (T)sym;
    }

    public <T extends Symbol> T resolveMAExpr(AST_Expr a) {
        if (a instanceof AST_Expr_PrimaryVariableName) {
            return (T)this.resolve(a.varname());
        } else if (a instanceof AST_Expr_OperBi) {
            AST_Expr_OperBi c = (AST_Expr_OperBi)a; Validate.isTrue(c.operator.equals("."));
            Symbol host = this.resolveMAExpr(c.left);
            return (T)host.resolveMember(c.right.varname());
        } else
            throw new IllegalStateException(a.toString());
    }

    public Symbol resolveMember(String name) {
        return Objects.requireNonNull(getSymbol(name), "Could not resolve member-symbol '"+name+"'");
    }

    @Override
    public String toString() {
        return "Symtab{" +
                "parent=" + parent +
                ", symbols=" + symbols +
                '}';
    }
}
