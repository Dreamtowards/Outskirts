package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

public class ParserRepeat extends Parser {

    /** the Parser to be repeat */
    private final Parser parser;

    /** just maximum only once. for use of Option. */
    private final boolean onlyonce;

    public ParserRepeat(Parser parser, boolean onlyonce) {
        this.parser = parser;
        this.onlyonce = onlyonce;
    }

//    @Override
//    public List<AST> read(Lexer lex) {
//        List<AST> ls = new ArrayList<>();
//        while (!lex.eof() && parser.match(lex)) {
//
//            ls.addAll(parser.read(lex));
//
//            if (onlyonce)
//                break;
//        }
//        return ls;
//    }

    @Override
    public void read(Lexer lex, List<AST> out) {
        while (!lex.eof() && parser.match(lex)) {

            parser.read(lex, out);

            if (onlyonce)
                break;
        }
    }

    @Override
    public boolean match(Lexer lex) {
        return parser.match(lex);
    }
}
