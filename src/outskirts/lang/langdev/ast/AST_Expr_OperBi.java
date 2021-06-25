package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.storage.dst.DObject;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_OperBi extends AST {

    private final AST left;
    private final String operator;
    private final AST right;

    public AST_Expr_OperBi(AST left, String operator, AST right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    public AST_Expr_OperBi(List<AST> ls) {
        this(ls.get(0), ((AST_Token)ls.get(1)).text(), ls.get(2));
        Validate.isTrue(ls.size() == 3);
    }

    public static AST composite(List<AST> ls) {
        int i = 0;
        AST l = ls.get(i++);
        while (i < ls.size()) {
            AST_Token o = (AST_Token)ls.get(i++);
            AST r = ls.get(i++);
            l = new AST_Expr_OperBi(l, o.text(), r);
        }
        return l;
    }


    @Override
    public GObject eval(Scope scope) {
        GObject l = left.eval(scope);
        GObject r = right.eval(scope);
        switch (operator) {
            case "+":
                return l.value instanceof String ?
                        new GObject((String)l.value + r.value) :
                        new GObject((float)l.value + (float)r.value);
            case "-": return new GObject((float)l.value - (float)r.value);
            case "*": return new GObject((float)l.value * (float)r.value);
            case "/": return new GObject((float)l.value / (float)r.value);
            case "=": {
                l.value = r.value;  // TODO: Type Not Sync.
                return l;
            }
            case "<": return  new GObject((float)l.value <  (float)r.value ? 1f : 0f);
            case "<=": return new GObject((float)l.value <= (float)r.value ? 1f : 0f);
            case ">": return  new GObject((float)l.value >  (float)r.value ? 1f : 0f);
            case ">=": return new GObject((float)l.value >= (float)r.value ? 1f : 0f);
            case "==": return new GObject((float)l.value == (float)r.value ? 1f : 0f);
            default:
                throw new IllegalStateException("Unsupported Operator '"+operator+"'.");
        }
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
