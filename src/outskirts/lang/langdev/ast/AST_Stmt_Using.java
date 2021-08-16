package outskirts.lang.langdev.ast;


public class AST_Stmt_Using extends AST_Stmt {


    public final boolean isStatic;
    public final AST_Expr used;  // OperBin or VarName

    public AST_Stmt_Using(boolean isStatic, AST_Expr used) {
        this.isStatic = isStatic;
        this.used = used;
    }

}
