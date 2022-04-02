package outskirts.lang.langdev.ast;


import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Stmt_Using extends AST_Stmt {

    private final boolean isStatic;
    private final AST_Expr used;  // MemberAccess / PrimaryIdentifer
    private final String asname;

    public AST_Stmt_Using(boolean isStatic, AST_Expr used, String asname) {
        this.isStatic = isStatic;
        this.used = used;
        this.asname = asname;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public AST_Expr getQualifiedExpression() {
        return used;
    }

    public String getDeclaredName() {
        return asname;
    }

}
