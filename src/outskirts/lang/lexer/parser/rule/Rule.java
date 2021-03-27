package outskirts.lang.lexer.parser.rule;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.syntax.Syntax;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public abstract class Rule {

    /**
     * Most times, return one element,
     * but for 'Rule-Repeat', 'Rule-Option', there may have multiplie or zero return element.
     * @param lexer after call, read-index always be modify.
     */
    public abstract List<Syntax> read(Lexer lexer);

    /**
     * is the context/lexer fits the Rule.
     * once there is fit, the rule was been choose. any exception occurred while read is exact exception.
     * @param lexer after call, read-index shouldn't be modify.
     */
    public abstract boolean fit(Lexer lexer);


    public final Syntax readone(Lexer lexer) {
        List<Syntax> l = read(lexer);
        Validate.isTrue(l.size() == 1);
        return l.get(0);
    }

}
