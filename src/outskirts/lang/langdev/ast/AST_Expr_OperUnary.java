package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.TokenType;

public class AST_Expr_OperUnary extends AST_Expr {

    private final AST_Expr expr;
    private final UnaryKind unarykind;

    public AST_Expr_OperUnary(AST_Expr expr, UnaryKind unarykind) {
        this.expr = expr;
        this.unarykind = unarykind;
    }

    public AST_Expr getExpression() {
        return expr;
    }

    public UnaryKind getUnaryKind() {
        return unarykind;
    }


    public enum UnaryKind {
        // Prefix
        NEG,    // - _ Negative
        POS,    // + _ Positive
        COMPL,  // ~ _ BitwiseComplement
        NOT,    // ! _ LogicalComplement
        REF,    // & _ Reference
        DEREF,  // * _ Dereference
        PRE_INC,  // ++ _ PreIncrease
        PRE_DEC,  // -- _ PreDecrease

        POST_INC, // _ ++ PostIncrease
        POST_DEC, // _ -- PostDecrease
        PTR_TYP;   // _ * PointerType

        public static UnaryKind of(TokenType oper, boolean post) {
            if (post) {
                if      (oper == TokenType.PLUSPLUS) return POST_INC;
                else if (oper == TokenType.SUBSUB)   return POST_DEC;
                else if (oper == TokenType.STAR)     return PTR_TYP;
                else throw new IllegalStateException("Illegal post unary operator.");
            }
            switch (oper) {
            case PLUSPLUS: return PRE_INC;
            case SUBSUB:   return PRE_DEC;
            case PLUS:     return POS;
            case SUB:      return NEG;
            case TILDE:    return COMPL;
            case BANG:     return NOT;
            case AMP:      return REF;
            case STAR:     return DEREF;
            default:       throw new IllegalStateException("Illegal pre unary operator.");
            }
        }
    }

    @Override
    public String toString() {
        return "{"+expr+getUnaryKind().name()+"}";
    }
}
