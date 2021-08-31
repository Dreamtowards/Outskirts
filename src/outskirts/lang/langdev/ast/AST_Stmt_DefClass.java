package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolClass;

import java.util.List;
// Things about Perfection. 21w32.
public class AST_Stmt_DefClass extends AST_Stmt implements AST.Modifierable{ // really? a stmt?

    private final String name;
    public SymbolClass sym;

    private final List<AST__Typename> superclasses;  // nullable.
    private final List<AST_Stmt> members;

    public AST__Modifiers modifiers;

    public AST_Stmt_DefClass(String name, List<AST_Expr_PrimaryIdentifier> genericParams, List<AST__Typename> superclasses, List<AST_Stmt> members) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    // getSimpleName() .?
    public String getSimpleName() {
        return name;
    }

    public List<AST__Typename> getSuperTypenames() {
        return superclasses;
    }

    public List<AST_Stmt> getMembers() {
        return members;
    }

    @Override
    public AST__Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtDefClass(this, p);
    }

    @Override
    public String toString() {
        return "(class "+ name +")";
    }

}
