package outskirts.lang.langdev.parser.paserconbinator;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.lexer.Lexer;

import java.util.List;

public class ParserLookaheadTag extends Parser {

    public static final Parser LOOKAHEAD_TAG = new ParserLookaheadTag();

    private ParserLookaheadTag() { }

//    @Override
//    public List<AST> read(Lexer lex) {
//        return Collections.emptyList();
//    }

    @Override
    public void read(Lexer lex, List<AST> out) { }

    @Override
    public boolean match(Lexer lex) {
        return true;
    }
}
