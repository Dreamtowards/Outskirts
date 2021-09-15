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
    private void _SetupSourceLoc(Lexer lx) {
        this.sourceloc = new SourceLoc(
                lx.sourceLocation,
                lx.getSource(),
                lx.readidx()-10,//lx.popReadIdx(),
                lx.readidx()
        );
    }
    {
        _SetupSourceLoc(Main.currLxr);
    }


    @Override
    public String toString() {
        return "AST::"+getClass().getSimpleName()+"::"+sourceloc;
    }

    public <P> void accept(ASTVisitor<P> visitor, P p) {
        throw new UnsupportedOperationException(getClass().getName());
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
