package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.lexer.TokenType;

public class AST_Expr_OperBinary extends AST_Expr {

    private final AST_Expr left;
    private final AST_Expr right;
    private final BinaryKind binarykind;

    public AST_Expr_OperBinary(AST_Expr left, AST_Expr right, BinaryKind binarykind) {
        this.left = left;
        this.right = right;
        this.binarykind = binarykind;
    }

    public AST_Expr getLeftOperand() {
        return left;
    }
    public AST_Expr getRightOperand() {
        return right;
    }

    public BinaryKind getBinaryKind() {
        return binarykind;
    }

    public enum BinaryKind {

        MUL, DIV,
        ADD, SUB,
        SHL, SHRS, SHRZ,
        LT, LTEQ, GT, GTEQ, IS,
        EQ, NEQ,
        BIT_AND, BIT_XOR, BIT_OR,
        LOG_AND, LOG_OR,
        ASSIGN;

        public static BinaryKind of(TokenType typ) {
            switch (typ) {
                case STAR:  return MUL;
                case SLASH: return DIV;
                case PLUS:  return ADD;
                case SUB:   return SUB;
                case LTLT:  return SHL;
                case GTGT:  return SHRS;
                case GTGTGT:return SHRZ;
                case LT:    return LT;
                case LTEQ:  return LTEQ;
                case GT:    return GT;
                case GTEQ:  return GTEQ;
                case IS:    return IS;
                case EQEQ:    return EQ;
                case BANGEQ:return NEQ;
                case AMP:   return BIT_AND;
                case CARET: return BIT_XOR;
                case BAR:   return BIT_OR;
                case AMPAMP:return LOG_AND;
                case BARBAR:return LOG_OR;
                case EQ:    return ASSIGN;
                default: throw new IllegalStateException();
            }
        }
    }

    @Override
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitExprOperBin(this, p);
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, binarykind.name(), right);
    }
}
