package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolClass;

import java.util.List;
// Things about Perfection. 21w32.
public class AST_Stmt_DefClass extends AST_Stmt implements AST.Modifierable{ // really? a stmt?

    public final String name;
    public SymbolClass sym;

    public final List<AST__Typename> superclasses;  // nullable.
    public final List<AST_Stmt> members;

    public AST__Modifiers modifiers;

    public AST_Stmt_DefClass(String name, List<AST_Expr_PrimaryIdentifier> genericParams, List<AST__Typename> superclasses, List<AST_Stmt> members) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
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
