//package outskirts.lang.langdev.parser.paserconbinator;
//
//import outskirts.lang.langdev.lexer.TokenItem;
//
//public class ParsingException extends RuntimeException {
//
//    public ParsingException(String message, TokenItem at) {
//        super(message+" at \""+at.text()+"\" ["+at.locationString()+"].");
//    }
//
//    public ParsingException(String message, TokenItem at, Throwable cause) {
//        this(message, at);
//        initCause(cause);
//    }
//}
