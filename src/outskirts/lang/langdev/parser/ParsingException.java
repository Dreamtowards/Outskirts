package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.lexer.Token;

public class ParsingException extends RuntimeException {

    public ParsingException(String message, Token at) {
        super(message+" at \""+at.text()+"\" ["+at.locationString()+"].");
    }

    public ParsingException(String message, Token at, Throwable cause) {
        this(message, at);
        initCause(cause);
    }
}
