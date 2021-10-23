package outskirts.lang.langdev.ast.astvisit;

import outskirts.lang.langdev.ast.*;

import java.util.List;

public interface ASTVisitor<P> {

    // needs default impl for iterates AST.children..? might doesn't needed now.

    default void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, P p)   { visitDefault(a, p); }
    default void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, P p)         { visitDefault(a, p); }

    default void visitExprFuncCall(AST_Expr_FuncCall a, P p)                   { visitDefault(a, p); }
    default void visitExprOperNew(AST_Expr_OperNew a, P p)                     { visitDefault(a, p); }
    default void visitExprOperNewMalloc(AST_Expr_OperNewMalloc a, P p)                     { visitDefault(a, p); }
    default void visitExprMemberAccess(AST_Expr_MemberAccess a, P p)           { visitDefault(a, p); }
    default void visitExprOperConditional(AST_Expr_OperConditional a, P p)     { visitDefault(a, p); }
    default void visitExprSizeOf(AST_Expr_OperSizeOf a, P p)                   { visitDefault(a, p); }
//    default void visitExprTmpDereference(AST_Expr_TmpDereference a, P p)       { visitDefault(a, p); }
//    default void visitExprTmpReference(AST_Expr_TmpReference a, P p)           { visitDefault(a, p); }
    default void visitExprOperUnary(AST_Expr_OperUnary a, P p)                 { visitDefault(a, p); }
    default void visitExprOperBinary(AST_Expr_OperBinary a, P p)               { visitDefault(a, p); }
    default void visitExprTypeCast(AST_Expr_TypeCast a, P p)                   { visitDefault(a, p); }
    // Lambda

    default void visitStmtBlock(AST_Stmt_Block a, P p)          { visitDefault(a, p); }
    default void visitStmtBreak(AST_Stmt_Break a, P p)          { visitDefault(a, p); }
    default void visitStmtContinue(AST_Stmt_Continue a, P p)    { visitDefault(a, p); }
    default void visitStmtNamespace(AST_Stmt_Namespace a, P p)  { visitDefault(a, p); }
    default void visitStmtUsing(AST_Stmt_Using a, P p)          { visitDefault(a, p); }
    default void visitStmtExpr(AST_Stmt_Expr a, P p)            { visitDefault(a, p); }
    default void visitStmtReturn(AST_Stmt_Return a, P p)        { visitDefault(a, p); }
    default void visitStmtIf(AST_Stmt_If a, P p)                { visitDefault(a, p); }
    default void visitStmtWhile(AST_Stmt_While a, P p)          { visitDefault(a, p); }
    default void visitStmtDefVar(AST_Stmt_DefVar a, P p)        { visitDefault(a, p); }
    default void visitStmtDefFunc(AST_Stmt_DefFunc a, P p)      { visitDefault(a, p); }
    default void visitStmtDefClass(AST_Stmt_DefClass a, P p)    { visitDefault(a, p); }

    default void visit_Annotation(AST__Annotation a, P p)           { visitDefault(a, p); }
    default void visit_CompilationUnit(AST__CompilationUnit a, P p) { visitDefault(a, p); }

    default void visitDefault(AST a, P p) {
        throw new UnsupportedOperationException();
    }


//    default void visit_Typename(AST__Typename a, P p)           { visitDefault(a, p); }
//    static <P> void _VisitStmts(ASTVisitor<P> visitor, List<AST_Stmt> stmts, P p) {
//        for (AST_Stmt stmt : stmts) {
//            stmt.accept(visitor, p);
//        }
//    }
//    static <P> void visit(AST a, ASTVisitor<P> visitor, P p) {
//        a.accept(visitor, p);
//    }
}
