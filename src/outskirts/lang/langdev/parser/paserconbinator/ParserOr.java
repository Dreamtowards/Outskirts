//package outskirts.lang.langdev.parser.paserconbinator;
//
//import outskirts.lang.langdev.ast.AST;
//import outskirts.lang.langdev.lexer.Lexer;
//
//import java.util.List;
//
//public class ParserOr extends Parser {
//
//    private final List<Parser> options;
//
//    public ParserOr(List<Parser> options) {
//        this.options = options;
//    }
//
////    @Override
////    public List<AST> read(Lexer lex) {
////        Parser p = choose(lex);
////        if (p == null)
////            throw new ParsingException("Bad OR. No matched option of "+options+".", lex.peek());
////        return p.read(lex);
////    }
//
//    @Override
//    public void read(Lexer lex, List<AST> out) {
//        Parser p = choose(lex);
//        if (p == null)
//            throw new ParsingException("Bad OR. No matched option of "+options+".", lex.peek());
//        p.read(lex, out);
//    }
//
//    @Override
//    public boolean match(Lexer lex) {
//        return choose(lex) != null;
//    }
//
//    private Parser choose(Lexer lex) {
//        for (Parser p : options) {
//            if (p.match(lex))
//                return p;
//        }
//        return null;
//    }
//}
