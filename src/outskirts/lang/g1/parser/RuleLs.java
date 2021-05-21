package outskirts.lang.g1.parser;

import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.g1.syntax.Syntax;
import outskirts.lang.g1.syntax.SyntaxNullStatement;
import outskirts.lang.g1.syntax.SyntaxToken;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.max;
import static java.util.Collections.singletonList;

public class RuleLs extends Rule {

    private List<Rule> rulelist = new ArrayList<>();

    /**
     * Look ahead N tokens when testing fit().
     */
    private int lookahead = 1;

    private boolean useLookaheadUntil = false;

    /**
     * For debugging.
     */
    private String rulename = "UndefinedName";
    private Throwable ruleCreateStack;

    /**
     * if is null, when read, just directly pass the results.  (results may > 1. e.g. applicated in patterned-arguments-passing.
     * if nonnull, when read, create a AST tree that holds the results.
     */
    private final Function<List<Syntax>, Syntax> createfunc;

    private RuleLs(Function<List<Syntax>, Syntax> createfunc) {
        this.createfunc = createfunc;
        ruleCreateStack = new Throwable();
    }

    public static RuleLs struct(Function<List<Syntax>, Syntax> createfunc) {
        return new RuleLs(Objects.requireNonNull(createfunc));
    }
    public static RuleLs pass() {
        return new RuleLs(null);
    }

    public static LinkedList<String> RuleReadStack = new LinkedList<>();

    @Override
    public List<Syntax> read(Lexer lexer) { try { RuleReadStack.push(rulename);
        List<Syntax> ls = new ArrayList<>();
        for (Rule rule : rulelist) {
            ls.addAll(rule.read(lexer));
        }
        if (createfunc == null) {
            Validate.isTrue(ls.size() != 0);
            return ls;
        } else {
            return singletonList(createfunc.apply(ls));
        }
                                            } finally { RuleReadStack.pop(); }
    }

    @Override
    public boolean fit(Lexer lexer) {
        // when empty, its allowed. (e.g. when just want create an empty AST.
        if (rulelist.isEmpty())
            return true;
        if (!useLookaheadUntil)
            return rulelist.get(0).fit(lexer);

        // do Lookahead Until
        int mark = lexer.index;
        try {
            for (Rule rule : rulelist) {
                if (rule instanceof RuleLookaheadUntil)
                    break;
                try {
                    rule.read(lexer);
                } catch (Exception ex) {
                    // not fit.
                    return false;
                }
            }
            return true;
        } finally {
            lexer.index = mark;  // set back.
        }
    }

    public RuleLs initname(String name) {
        rulename = name;
        return this;
    }
//    public final RuleLs lookahead(int n) {
//        Validate.isTrue(n >= 1);
//        lookahead = n;
//        return this;
//    }
    public final RuleLs markLookaheadUntil() {
        useLookaheadUntil = true;
        return and(new RuleLookaheadUntil());
    }

    public final RuleLs and(Rule rule) {
        rulelist.add(rule);
        return this;
    }



    private RuleLs token(Function<Token, Syntax> createfunc, Consumer<Token> validator) {
        return and(new RuleToken(createfunc, validator));
    }
    public final RuleLs id(String id) {
        return token(null, t -> {
            Validate.isTrue((t.isBorder() || t.isName()) && t.text().equals(id), "Required \"%s\" (Type: BORDER or NAME). actually: %s. "+RuleReadStack.toString(), id, t.detailString());
        });
    }

    public final RuleLs number(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isNumber(), "Required Type: NUMBER. actually: %s", t.detailString());
        });
    }

    public final RuleLs name(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isName(), "Required Type: NAME. actually: %s. "+RuleReadStack, t.detailString());
        });
    }
    public final RuleLs name() {
        return name(SyntaxToken::new);
    }

    public final RuleLs string(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isString(), "Required Type: STRING. actually: %s", t.detailString());
        });
    }


    /** Rule-Repeat. repeat 0-n times. */
    public final RuleLs repeat(Rule rule) {
        return and(new RuleRepeat(rule, false));
    }

    /** Rule-Option. 'repeat' 0-1 times. */
    public final RuleLs op(Rule rule) {
        return and(new RuleRepeat(rule, true));
    }



    public final RuleLs or(Rule... ruleoptions) {
        return and(new RuleOr(ruleoptions));
    }
    public final RuleLs ornull(Rule... ruleoptions) {
        return or(CollectionUtils.concat(ruleoptions, struct(SyntaxNullStatement::new)));
    }
}
