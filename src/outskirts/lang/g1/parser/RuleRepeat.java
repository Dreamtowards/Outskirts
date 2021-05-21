package outskirts.lang.g1.parser;

import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.g1.syntax.Syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Repeat 0-n times.
 */
public class RuleRepeat extends Rule {

    /** the rule to repeat. */
    private Rule rule;

    /** for Rule-Option. */
    private boolean onlyonce;

    public RuleRepeat(Rule rule, boolean onlyonce) {
        this.rule = rule;
        this.onlyonce = onlyonce;
    }

    @Override
    public List<Syntax> read(Lexer lexer) {
        List<Syntax> ls = new ArrayList<>();
        while (rule.fit(lexer)) {

            ls.addAll(rule.read(lexer));

            if (onlyonce)
                break;
        }
        return ls;
    }

    @Override
    public boolean fit(Lexer lexer) {
        return rule.fit(lexer);
    }
}
