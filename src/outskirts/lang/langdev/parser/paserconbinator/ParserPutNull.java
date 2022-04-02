package outskirts.lang.langdev.parser.paserconbinator;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.lexer.Lexer;

import java.util.List;

/**
 * Just 'simple' put null on read.
 * use for opnull.
 */
public class ParserPutNull extends Parser {

    public static final ParserPutNull INST = new ParserPutNull();

    @Override
    public void read(Lexer lex, List<AST> out) {
        out.add(null);
    }

    @Override
    public boolean match(Lexer lex) {
        return true;
    }
}
