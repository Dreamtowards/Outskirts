//package outskirts.lang.langdev.parser.paserconbinator;
//
//import outskirts.lang.langdev.ast.AST;
//import outskirts.lang.langdev.ast.AST__Token;
//import outskirts.lang.langdev.lexer.Lexer;
//import outskirts.lang.langdev.lexer.Token;
//
//import java.util.List;
//import java.util.function.Function;
//
//public class ParserToken extends Parser {
//
//    /** Token validator: return null means pass, else just error message. */
//    private Function<Token, String> validator;
//
//    /** is needs create AST_Token. most times is true. false only when just validate but doesn't need the result */
//    private boolean create = true;
//
//    public ParserToken(Function<Token, String> validator, boolean create) {
//        this.validator = validator;
//        this.create = create;
//    }
//
////    @Override
////    public List<AST> read(Lexer lex) {
////        Token t = lex.next();
////
////        String err = validator.apply(t);
////        if (err != null) throw new ParsingException("Bad token: "+err, t);
////
////        if (createfunc != null) {
////            return singletonList(createfunc.apply(t));
////        } else {
////            return emptyList();
////        }
////    }
//
//    @Override
//    public void read(Lexer lex, List<AST> out) {
//        Token t = lex.next();
//
//        String err = validator.apply(t);
//        if (err != null) throw new ParsingException("Bad token: "+err, t);
//
//        if (create) {
//            out.add(new AST__Token(t));
//        }
//    }
//
//    @Override
//    public boolean match(Lexer lex) {
//        return validator.apply(lex.peek()) == null;
//    }
//}
