package outskirts.lang.langdev.lexer;

import outskirts.util.Validate;

import java.util.Objects;
import java.util.function.Predicate;

public final class Token {

//    public static final String EOF_T = "\0";
//    public static final Token  EOF = new Token(EOF_T, 0, 0, 0, false);
//    public static final int TYPE_EOF = 0;
//    public static final int TYPE_NAME = 1;
//    public static final int TYPE_KEYWORD = 2;
//    public static final int TYPE_INT = 3;
//    public static final int TYPE_INT_LONG = 4;
//    public static final int TYPE_FLOAT = 5;
//    public static final int TYPE_FLOAT_DOUBLE = 6;
//    public static final int TYPE_CHAR = 7;
//    public static final int TYPE_STRING = 8;
//    public static final String[] TYPES_NAME = {"EOF", "NAME", "KEYWORD", "INT", "INT_LONG", "FLOAT", "FLOAT_DOUBLE", "CHAR", "STRING"};

    private final String content;  // only available for IDENTIFIER, STRING, INT, FLOAT, CHAR etc.
    private final TokenType type;

    // connNext or connPrev .?
//    private final boolean isConnectedNext;

    public SourceLoc sloc;  // sourcelocation.

    public Token(TokenType type, String content, SourceLoc sloc) {
        if (type.fixed() == null)
            Validate.isTrue(content != null);
        this.content = content;
        this.type = type;
        this.sloc = sloc;
    }

    public String content() {
        return content;
    }

    public TokenType type() {
        return type;
    }

//    public final int getLineNumber() {
//        return lineNumber;
//    }
//    public final int getCharNumber() {
//        return charNumber;
//    }
//    public final boolean isName() { return type == TYPE_NAME; }
//    public final boolean isKeyword() { return type == TYPE_KEYWORD; }
//    public final boolean isInt() { return type == TYPE_INT; }
//    public final boolean isIntLong() { return type == TYPE_INT_LONG; }
//    public final boolean isFloat() { return type == TYPE_FLOAT; }
//    public final boolean isFloatDouble() { return type == TYPE_FLOAT_DOUBLE; }
//    public final boolean isString() { return type == TYPE_STRING; }
//    public final boolean isChar()   { return type == TYPE_CHAR; }


    @Override
    public String toString() {
        return type.name();
    }

    public String detailString() {
        return String.format("%s('%s')[%s]", type.name(), Objects.requireNonNull(type.fixed() != null ? type.fixed() : content), locationString());
    }
    public String locationString() {
        return (sloc.getLineNumber()+1)+":"+(sloc.getCharNumber()+1);
    }

//    public boolean isConnectedNext() {
//        return isConnectedNext;
//    }

    public Token validate(Predicate<Token> pred) {
        if (!pred.test(this))
            throw new IllegalStateException();
        return this;
    }
}
