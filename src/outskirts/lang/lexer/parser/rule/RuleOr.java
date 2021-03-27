package outskirts.lang.lexer.parser.rule;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.syntax.Syntax;
import outskirts.util.Validate;

import java.util.List;

/**
 * Must select one.
 */
public class RuleOr extends Rule {

    private Rule[] ruleoptions;

    public RuleOr(Rule[] ruleoptions) {
        this.ruleoptions = ruleoptions;
    }

    @Override
    public List<Syntax> read(Lexer lexer) {
        Rule rule = chooseOption(lexer);
        Validate.isTrue(rule != null, "Not available option in OR choose.  (Near: \"%s\"", lexer.peek());

        return rule.read(lexer);
    }

    @Override
    public boolean fit(Lexer lexer) {
        return chooseOption(lexer) != null;
    }

    private Rule chooseOption(Lexer lexer) {
        for (Rule rule : ruleoptions) {
            if (rule.fit(lexer))
                return rule;
        }
        return null;
    }
}
