package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.Main;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.SourceLoc;
import outskirts.lang.langdev.symtab.Scope;

/**
 * AST_Stmt
 * pkg
 * AST_Stmt_Package
 * AST_Stmt_Using
 * AST_Stmt_DefClass
 * norm
 * AST_Stmt_Block
 * AST_Stmt_Blank
 * AST_Stmt_DefFunc
 * AST_Stmt_DefVar
 * AST_Stmt_Expr
 * AST_Stmt_Return
 * AST_Stmt_If
 * AST_Stmt_While
 *
 * AST_Expr
 * AST_Expr_FuncCall
 * AST_Expr_Lambda
 * AST_Expr_MemberAccess
 * AST_Expr_OperBin
 * AST_Expr_OperNew
 * AST_Expr_OperTriCon
 * AST_Expr_OperUnaryPost
 * AST_Expr_OperUnaryPre
 * AST_Expr_PLNumber
 * AST_Expr_PLString
 * AST_Expr_PLVarName
 */

public abstract class AST {

    // public Scope scope;

//    private String sourcefile;
//    private int sourcefile_pos; startpos, endpos.

    public SourceLoc sourceloc;

    /**
     * Context Required: lx.pushReadIdx() at AST-Parsing-Beginning.
     */
    public <T> T _SetupSourceLoc(Lexer lx, int begRdi) {
        this.sourceloc = new SourceLoc(
                lx.getSourceName(),
                lx.getSource(),
                begRdi,
                lx.readidx()
        );
        return (T)this;
    }


    @Override
    public final String toString() {
        return getClass().getSimpleName()+"::"+sourceloc;
    }

    /**
     * Dont allows subclass override.
     * because we need unified catches exceptions.
     * somepeoplesay, this might be suspected violates 'OCP principle', but I think that's meaningless there.
     * there is similar operations, better to centerized manage. taugh grab resposibility, taugh cohesion.
     */
    public final <P> void accept(ASTVisitor<P> visitor, P p) {
        try {
            if (this instanceof AST__Annotation)                visitor.visit_Annotation((AST__Annotation)this, p);
            else if (this instanceof AST__CompilationUnit)      visitor.visit_CompilationUnit((AST__CompilationUnit)this, p);
            // else if (this instanceof AST__Modifiers)        visitor.visitM
// Expression
            else if (this instanceof AST_Expr_FuncCall)         visitor.visitExprFuncCall((AST_Expr_FuncCall)this, p);
            // else if (this instanceof AST_Expr_Lambda)
            else if (this instanceof AST_Expr_MemberAccess)     visitor.visitExprMemberAccess((AST_Expr_MemberAccess)this, p);
            else if (this instanceof AST_Expr_OperBinary)       visitor.visitExprOperBinary((AST_Expr_OperBinary)this, p);
            else if (this instanceof AST_Expr_OperConditional)  visitor.visitExprOperConditional((AST_Expr_OperConditional)this, p);
            else if (this instanceof AST_Expr_OperNew)          visitor.visitExprOperNew((AST_Expr_OperNew)this, p);
            else if (this instanceof AST_Expr_OperNewMalloc)    visitor.visitExprOperNewMalloc((AST_Expr_OperNewMalloc)this, p);
            else if (this instanceof AST_Expr_OperSizeOf)       visitor.visitExprSizeOf((AST_Expr_OperSizeOf)this, p);
            else if (this instanceof AST_Expr_OperUnary)        visitor.visitExprOperUnary((AST_Expr_OperUnary)this, p);
            else if (this instanceof AST_Expr_PrimaryIdentifier)visitor.visitExprPrimaryIdentifier((AST_Expr_PrimaryIdentifier)this, p);
            else if (this instanceof AST_Expr_PrimaryLiteral)   visitor.visitExprPrimaryLiteral((AST_Expr_PrimaryLiteral)this, p);
            else if (this instanceof AST_Expr_TypeCast)         visitor.visitExprTypeCast((AST_Expr_TypeCast)this, p);
// Statement
            else if (this instanceof AST_Stmt_Blank)    ;
            else if (this instanceof AST_Stmt_Block)    visitor.visitStmtBlock((AST_Stmt_Block)this, p);
            else if (this instanceof AST_Stmt_DefClass) visitor.visitStmtDefClass((AST_Stmt_DefClass)this, p);
            else if (this instanceof AST_Stmt_DefFunc)  visitor.visitStmtDefFunc((AST_Stmt_DefFunc)this, p);
            else if (this instanceof AST_Stmt_DefVar)   visitor.visitStmtDefVar((AST_Stmt_DefVar)this, p);
            else if (this instanceof AST_Stmt_Expr)     visitor.visitStmtExpr((AST_Stmt_Expr)this, p);
            else if (this instanceof AST_Stmt_If)       visitor.visitStmtIf((AST_Stmt_If)this, p);
            else if (this instanceof AST_Stmt_Namespace)visitor.visitStmtNamespace((AST_Stmt_Namespace)this, p);
            else if (this instanceof AST_Stmt_Return)   visitor.visitStmtReturn((AST_Stmt_Return)this, p);
            else if (this instanceof AST_Stmt_Using)    visitor.visitStmtUsing((AST_Stmt_Using)this, p);
            else if (this instanceof AST_Stmt_While)    visitor.visitStmtWhile((AST_Stmt_While)this, p);
            else throw new UnsupportedOperationException(getClass().getName());
        } catch (Exception ex) {
            throw new Error("Failed visit "+this+".", ex);
        }
    }

//    public Kind getKind() {
//        throw new UnsupportedOperationException();
//    }


    public interface Modifierable {

        AST__Modifiers getModifiers();

    }

//    public enum Kind {
//
//        Annotation,
//        CompilationUnit,
//        Modifiers,
//
//
//    }

}
