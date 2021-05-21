package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {

//    public abstract List<AST> read(Lexer lex);

    public abstract void read(Lexer lex, List<AST> out);

    /**
     * Directly pass the onlyone AST out.
     * used in Parserls nonnull-createfunc.
     */
    public final AST readone(Lexer lex) {
        List<AST> ls = new ArrayList<>();
        read(lex, ls);
        Validate.isTrue(ls.size() == 1);
        return ls.get(0);
    }

    /**
     * the match(), mainly use for ParserOr. to choose road.
     *
     * after call of match(), the index of lexer should as same as before call.
     */
    public abstract boolean match(Lexer lex);

}
