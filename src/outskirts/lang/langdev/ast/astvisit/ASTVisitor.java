package outskirts.lang.langdev.ast.astvisit;

import outskirts.lang.langdev.ast.*;

import java.util.List;

public interface ASTVisitor<P> {

    void visitExprFuncCall(AST_Expr_FuncCall a, P p);
    void visitExprMemberAccess(AST_Expr_MemberAccess a, P p);
    // Lambda
    void visitExprOperBin(AST_Expr_OperBi a, P p);
    void visitExprOperNew(AST_Expr_OperNew a, P p);
    void visitExprOperTriCon(AST_Expr_OperConditional a, P p);
    void visitExprOperUnary(AST_Expr_OperUnary a, P p);
    void visitExprSizeOf(AST_Expr_OperSizeOf a, P p);
    // Float, String
    void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, P p);
    void visitExprPrimaryLiteralInt(AST_Expr_PrimaryLiteralInt a, P p);
    void visitExprPrimaryLiteralChar(AST_Expr_PrimaryLiteralChar a, P p);

    void visitStmtBlock(AST_Stmt_Block a, P p);
    void visitStmtDefClass(AST_Stmt_DefClass a, P p);  // ClassMember?
    void visitStmtDefFunc(AST_Stmt_DefFunc a, P p);  // FuncParam?
    void visitStmtDefVar(AST_Stmt_DefVar a, P p);
    void visitStmtExpr(AST_Stmt_Expr a, P p);
    void visitStmtIf(AST_Stmt_If a, P p);
    void visitStmtNamespace(AST_Stmt_Namespace a, P p);
    void visitStmtReturn(AST_Stmt_Return a, P p);
    void visitStmtUsing(AST_Stmt_Using a, P p);
    void visitStmtWhile(AST_Stmt_While a, P p);

    void visit_Annotation(AST__Annotation a, P p);
    void visit_Typename(AST__Typename a, P p);
    void visit_CompilationUnit(AST__CompilationUnit a, P p);



    static <P> void _VisitStmts(ASTVisitor<P> visitor, List<AST_Stmt> stmts, P p) {
        for (AST_Stmt stmt : stmts) {
            stmt.accept(visitor, p);
        }
    }

//    static <P> void visit(AST a, ASTVisitor<P> visitor, P p) {
//        a.accept(visitor, p);
//    }
}
