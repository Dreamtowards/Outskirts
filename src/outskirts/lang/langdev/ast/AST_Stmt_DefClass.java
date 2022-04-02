package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.TypeSymbol;

import java.util.List;
// Things about Perfection. 21w32.
public class AST_Stmt_DefClass extends AST_Stmt implements AST.Modifierable{ // really? a stmt?

    private final String name;
    public SymbolClass sym;

    private final List<AST_Expr> superclasses;  // nullable.
    private final List<AST_Stmt> members;

    private final List<AST_Expr_PrimaryIdentifier> genericsparams;

    private final AST__Modifiers modifiers;

    public List<TypeSymbol> tmpGenericsArguments;  // for instancing
    public SymbolClass tmpGenericsInstance;  // result

    public AST_Stmt_DefClass(String name, List<AST_Expr> superclasses, List<AST_Stmt> members, AST__Modifiers modifiers, List<AST_Expr_PrimaryIdentifier> genericParams) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        this.modifiers = modifiers;
        this.genericsparams = genericParams;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    // getSimpleName() .?
    public String getSimpleName() {
        return name;
    }

    // Clause
    public List<AST_Expr> getSuperTypeExpressions() {
        return superclasses;
    }

    public List<AST_Stmt> getMembers() {
        return members;
    }

    public List<AST_Expr_PrimaryIdentifier> getGenericsParameters() {
        return genericsparams;
    }

    @Override
    public AST__Modifiers getModifiers() {
        return modifiers;
    }

}
