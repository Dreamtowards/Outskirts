package outskirts.lang.g1.parser;

import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.g1.syntax.Syntax;

import java.util.Collections;
import java.util.List;

/**
 * This is actually not a rule. just a placeholder/tag. for identify the 'Fit-Lookahead' stop-position.
 */
public final class RuleLookaheadUntil extends Rule {

    @Override
    public List<Syntax> read(Lexer lexer) {
        return Collections.emptyList();
    }

    @Override
    public boolean fit(Lexer lexer) {
        return true;
    }
}
