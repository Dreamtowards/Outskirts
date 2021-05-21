package outskirts.lang.g1.parser.expr;

import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.g1.parser.Rule;
import outskirts.lang.g1.syntax.Syntax;
import outskirts.lang.g1.syntax.SyntaxToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public class RuleExpr extends Rule {

    /**
     * AST-Expr create. arg-list size == 3.
     */
    private Function<List<Syntax>, Syntax> createfunc;

    private Rule factor;

    public RuleExpr(Function<List<Syntax>, Syntax> createfunc, Rule factor) {
        this.createfunc = createfunc;
        this.factor = factor;
    }

    /**
     * @return a single factor or an bi-expr.
     */
    @Override
    public List<Syntax> read(Lexer lexer) {
        Syntax left = factor.readone(lexer);
        Operator oper;
        while ((oper = nextOperator(lexer)) != null) {
            left = readRightside(lexer, left, oper.priority);
        }
        return singletonList(left);
    }

    private Syntax readRightside(Lexer lexer, Syntax left, int priority) {
        List<Syntax> ls = new ArrayList<>();
        ls.add(left);
        ls.add(new SyntaxToken(lexer.next()));  // the operator.
        Syntax right = factor.readone(lexer);
        Operator next;
        while ((next = nextOperator(lexer)) != null && isNextOperatorHigherPriority(priority, next)) {
            right = readRightside(lexer, right, next.priority);
        }
        ls.add(right);
        return createfunc.apply(ls);
    }

    private boolean isNextOperatorHigherPriority(int priority, Operator next) {
        if (next.assocside == Operator.ASS_RIGHT)
            return priority <= next.priority;
        else
            return priority < next.priority;
    }

    private Operator nextOperator(Lexer lexer) {
        Token t = lexer.peek();
        if (t == null) return null;
        return Operator.get(t.text());  // may null (not such oper).
    }

    @Override
    public boolean fit(Lexer lexer) {
        return factor.fit(lexer);
    }
}
