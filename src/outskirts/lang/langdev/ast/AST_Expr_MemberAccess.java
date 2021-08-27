package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

/**
 * MemberAccess: expr . identifier
 * its can been a UnaryOperator, but lots time, operator'.' check and rhs identifier are manual tackled,
 * and more, MemberAccess type can showing more type-info.
 *
 * but there seems have a problem: the lhs expr, may is others expr, e.g. new Typ().memberIden (a.func()).memberIden
 */
public class AST_Expr_MemberAccess extends AST_Expr {

    public final AST_Expr expr;  // when as an executable-expression, its not-only Identifier / MemberAccess
    public final String identifier;

    public AST_Expr_MemberAccess(AST_Expr expr, String identifier) {
        this.expr = expr;
        this.identifier = identifier;
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprMemberAccess(this, p);
    }
}
