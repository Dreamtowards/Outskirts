package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.util.Validate;

public class AST_Expr_PrimaryLiteral extends AST_Expr {

    private Object val;
    private LiteralKind literalkind;

    public AST_Expr_PrimaryLiteral(Object val, LiteralKind literalkind) {
        this.val = val;
        this.literalkind = literalkind;
    }

    public int    getInt32() {   Validate.isTrue(literalkind == LiteralKind.INT32);   return (int) val;   }
    public long   getInt64() {   Validate.isTrue(literalkind == LiteralKind.INT64);   return (long)val;   }
    public float  getFloat32() { Validate.isTrue(literalkind == LiteralKind.FLOAT32); return (float)val;  }
    public double getFloat64() { Validate.isTrue(literalkind == LiteralKind.FLOAT64); return (double)val; }
    public char   getChar()    { Validate.isTrue(literalkind == LiteralKind.CHAR);    return (char)val;   }
    public String getString()  { Validate.isTrue(literalkind == LiteralKind.STRING);  return (String)val; }
    public boolean getBool()   { Validate.isTrue(literalkind == LiteralKind.BOOL);    return (boolean)val;}

    public LiteralKind getLiteralKind() {
        return literalkind;
    }

    public enum LiteralKind {
        INT32,
        INT64,
        FLOAT32,
        FLOAT64,
        CHAR,
        STRING,
        BOOL
    }

}
