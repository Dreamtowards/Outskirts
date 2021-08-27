package outskirts.lang.langdev.ast;


import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

public class AST_Stmt_Using extends AST_Stmt {


    public final boolean isStatic;
    public final AST_Expr used;  // OperBin or VarName
    public final String asname;

    public AST_Stmt_Using(boolean isStatic, AST_Expr used, String asname) {
        this.isStatic = isStatic;
        this.used = used;
        this.asname = asname;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtUsing(this, p);
    }
}
