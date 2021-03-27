package outskirts.lang.lexer.parser;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.syntax.Syntax;
import outskirts.lang.syntax.SyntaxBiExpression;
import outskirts.lang.syntax.SyntaxNullStatement;
import outskirts.lang.syntax.SyntaxToken;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.singletonList;

/**
 * Rule List.
 *
 * tried: One Rule Base-class. failed: not good flexable,
 *   1. cause creating of AST/Syntax object needs defined in 'each' sub-class.
 *   2. the cause more layer.
 */
public final class RuleLs {

    private List<RuleComponent> rules = new ArrayList<>();

    /**
     * if nonnull, create a AST Syntax Object on read.  if null, just directly return/pass the onlyone Syntax result on read.
     */
    private Function<List<Syntax>, Syntax> createfunc;

    public RuleLs(Function<List<Syntax>, Syntax> createfunc) {
        this.createfunc = createfunc;
    }

    public final Syntax read(Lexer lexer) {
        List<Syntax> ls = new ArrayList<>();
        for (RuleComponent e : rules) {
            ls.addAll(e.read(lexer));
        }
        if (createfunc == null) {
            // directly passby.
            Validate.isTrue(ls.size() == 1);
            return ls.get(0);
        } else {
            // create AST, layout.
            return createfunc.apply(ls);
        }
    }

    public static RuleLs struc(Function<List<Syntax>, Syntax> createfunc) {
        return new RuleLs(createfunc);
    }
    public static RuleLs pass() {
        return new RuleLs(null);
    }

    public RuleLs and(RuleComponent rule) {
        rules.add(rule);
        return this;
    }
    public RuleLs and(RuleLs rulels) {
        return and(new RuleLs.Ls(rulels));
    }

    private RuleLs token(Function<Token, Syntax> createfunc, Consumer<Token> validator) {
        return and(new RuleLs.CToken(createfunc, validator));
    }

    public final RuleLs id(String id) {
        return token(null, t -> {
            Validate.isTrue(t.isName() || t.isBorder(), "Required type: Name or Border.");
            Validate.isTrue(t.text().equals(id), "Expected: '%s', actually: %s", id, t.detailString());
        });
    }

    public final RuleLs name(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isName(), "Expected type: Name, actually: "+t.detailString());
        });
    }
    public final RuleLs name() {
        return name(SyntaxToken::new);
    }

    public final RuleLs number(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isNumber(), "Expected type: Number, actually: " + t.detailString());
        });
    }
    public final RuleLs number() {
        return number(SyntaxToken::new);
    }

    public final RuleLs string(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isString(), "Expected type: String, actually: " + t.detailString());
        });
    }



    /** Rule-Repeat. repeat 0 or Unlimited times. */
    public final RuleLs repeat(RuleLs e) {
        return and(new RuleLs.Repeat(e, false));
    }
    /** Rule-Optional. repeat 0 or 1 times. */
    public final RuleLs op(RuleLs e) {
        return and(new RuleLs.Repeat(e, true));
    }
    /** Rule-Or. must select one of options. */
    public final RuleLs or(RuleLs... e) {
        return and(new RuleLs.Or(e));
    }
    public final RuleLs ornull(RuleLs... e) {
        return or(CollectionUtils.concat(e, struc(SyntaxNullStatement::new)));
    }

    /** Rule-Expr. a specual-construction-rule. a bi-expression or just a factor */
    public final RuleLs expr(RuleLs factor) {
        return and(new RuleLs.Expr(factor));
    }

    /**
     * Rule-Component of Rule-List
     */
    public static abstract class RuleComponent {


        public abstract List<Syntax> read(Lexer lexer);

    }

    public static class Id extends RuleComponent {
        private final String id;
        public Id(String id) { this.id = id; }

        @Override
        public List<Syntax> read(Lexer lexer) {
            Token t = lexer.next();
            return Collections.emptyList();  // just skip.
        }
    }

    public static class CToken extends RuleComponent {
        private Function<Token, Syntax> createfunc;
        private Consumer<Token> validator;
        public CToken(Function<Token, Syntax> createfunc, Consumer<Token> validator) {
            this.createfunc = createfunc;
            this.validator = validator;
        }

        @Override
        public List<Syntax> read(Lexer lexer) {
            Token t = lexer.next();
            validator.accept(t);
            if (createfunc == null)
                return Collections.emptyList();
            return singletonList(createfunc.apply(t));
        }
    }

    public static class Repeat extends RuleComponent {
        private RuleLs e;
        private boolean onlyonce;  // for 'option-rule'.
        public Repeat(RuleLs e, boolean onlyonce) { this.e = e; this.onlyonce = onlyonce; }

        @Override
        public List<Syntax> read(Lexer lexer) {
            List<Syntax> ls = new ArrayList<>();
            int i = lexer.index;
            try {
                while (true) {
                    ls.add(e.read(lexer));
                    i = lexer.index;
                    if (onlyonce)
                        break;
                }
            } catch (Exception ex) {
                // rule not match.
                // ex.printStackTrace();
                lexer.index = i;  // rollback.
            }
            return ls;
        }
    }

    public static class Or extends RuleComponent {
        private RuleLs[] es;
        public Or(RuleLs... es) { this.es = es; }

        @Override
        public List<Syntax> read(Lexer lexer) {
            int i = lexer.index;
            for (RuleLs e : es) {
                try {
                    return singletonList(e.read(lexer));
                } catch (Exception ex) {
                    // not match
                    // ex.printStackTrace();
                    lexer.index = i;  // setback.
                }
            }
            throw new IllegalStateException("'OR' not matched"+lexer.peek());
        }
    }

    public static class Ls extends RuleComponent {
        RuleLs rulels;
        public Ls(RuleLs rulels) { this.rulels = rulels; }

        @Override
        public List<Syntax> read(Lexer lexer) {
            return singletonList(rulels.read(lexer));
        }
    }

    public static class Expr extends RuleComponent {
        private RuleLs factor;
        public Expr(RuleLs factor) { this.factor = factor; }
        public void initfactor(RuleLs factor) {
            this.factor = factor;
        }

        @Override
        public List<Syntax> read(Lexer lexer) {
            Syntax left = factor.read(lexer);
            Operator oper;
            while ((oper = nextOperator(lexer)) != null) {
                left = doShift(lexer, left, oper.priority);
            }
            return singletonList(left);
        }
        private Syntax doShift(Lexer lexer, Syntax left, int priority) {
            List<Syntax> ls = new ArrayList<>();
            ls.add(left);
            ls.add(new SyntaxToken(lexer.next()));
            Syntax right = factor.read(lexer);
            Operator next;
            while ((next = nextOperator(lexer)) != null && rightIsExpr(priority, next)) {
                right = doShift(lexer, right, next.priority);
            }
            ls.add(right);
            return new SyntaxBiExpression(ls);
        }
        private static Operator nextOperator(Lexer lexer) {
            Token t = lexer.peek();
            if (t != null && t.isBorder())
                return Operator.get(t.text());
            return null;
        }
        private static boolean rightIsExpr(int priority, Operator next) {
            if (next.assocside == Operator.ASS_LEFT)
                return priority < next.priority;
            else
                return priority <= next.priority;
        }
    }
}
