package outskirts.lang.lexer.parser.rule;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.syntax.Syntax;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class RuleToken extends Rule {

    /**
     * if is null, when read, just return empty. (applicated in skip identifiers/keywords.
     * if nunnull, when read, just as usually, create.
     */
    private Function<Token, Syntax> createfunc;  // todo: think about this.? lot-wheres have the createfunc. can just use one way solves.?

    /**
     * Validation for Token. throw an exception if invalid.
     * (instead of return an boolean for isPass(), we use excptions that can gives detailed reasons)
     */
    private Consumer<Token> validator;

    public RuleToken(Function<Token, Syntax> createfunc, Consumer<Token> validator) {
        this.createfunc = createfunc;
        this.validator = validator;
    }

    @Override
    public List<Syntax> read(Lexer lexer) {
        Token t = lexer.next();
        validator.accept(t);  // validate

        return createfunc == null ? emptyList() : singletonList(createfunc.apply(t));
    }

    @Override
    public boolean fit(Lexer lexer) {
        try {
            validator.accept(lexer.peek());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
