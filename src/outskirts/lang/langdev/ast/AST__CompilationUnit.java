package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.util.Validate;

import java.util.List;

/**
 * CompilationUnit is a little differ to a Block. the terminal '\0'(EOF) vs. '}'
 */
public class AST__CompilationUnit extends AST {

    private final List<AST_Stmt> stmts;

    public AST__CompilationUnit(List<AST_Stmt> stmts) {
        stmts.forEach(e -> Validate.isTrue(e instanceof AST_Stmt_Namespace || e instanceof AST_Stmt_Using || e instanceof AST_Stmt_DefClass));
        this.stmts = stmts;
    }

    public List<AST_Stmt> getDeclrations() {
        return stmts;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visit_CompilationUnit(this, p);
    }
}
