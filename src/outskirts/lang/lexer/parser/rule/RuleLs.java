package outskirts.lang.lexer.parser.rule;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.syntax.Syntax;
import outskirts.lang.syntax.SyntaxConstantNumber;
import outskirts.lang.syntax.SyntaxNullStatement;
import outskirts.lang.syntax.SyntaxToken;
import outskirts.util.CollectionUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public class RuleLs extends Rule {

    private List<Rule> rulelist = new ArrayList<>();
    private int lookahead = 1;

    /**
     * if is null, when read, just directly pass the results.  (results may > 1. e.g. applicated in patterned-arguments-passing.
     * if nonnull, when read, create a AST tree that holds the results.
     */
    private Function<List<Syntax>, Syntax> createfunc;

    private RuleLs(Function<List<Syntax>, Syntax> createfunc) {
        this.createfunc = createfunc;
    }

    public static RuleLs struct(Function<List<Syntax>, Syntax> createfunc) {
        return new RuleLs(Objects.requireNonNull(createfunc));
    }
    public static RuleLs pass() {
        return new RuleLs(null);
    }


    @Override
    public List<Syntax> read(Lexer lexer) {
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
    }

    @Override
    public boolean fit(Lexer lexer) {
        // when empty, its allowed. (e.g. when just want create an empty AST.
        if (rulelist.isEmpty())
            return true;
        for (int i = 0;i < lookahead;i++) {
            if (!rulelist.get(i).fit(lexer))
                return false;
        }
        return true;
    }

    public final RuleLs lookahead(int n) {
        Validate.isTrue(n >= 1);
        lookahead = n;
        return this;
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
            Validate.isTrue((t.isBorder() || t.isName()) && t.text().equals(id), "Required \"%s\" (Type: BORDER or NAME). actually: %s", id, t.detailString());
        });
    }

    public final RuleLs number(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isNumber(), "Required Type: NUMBER. actually: %s", t.detailString());
        });
    }

    public final RuleLs name(Function<Token, Syntax> createfunc) {
        return token(createfunc, t -> {
            Validate.isTrue(t.isName(), "Required Type: NAME. actually: %s", t.detailString());
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
    public final Rule ornull(Rule... ruleoptions) {
        return or(CollectionUtils.concat(ruleoptions, struct(SyntaxNullStatement::new)));
    }
}
