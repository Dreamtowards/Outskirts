package outskirts.lang.langdev.lexer;

import java.util.Objects;
import java.util.function.Predicate;

public final class Token {

    public static final String EOF_T = "\0";
    public static final Token  EOF = new Token(EOF_T, 0, 0, 0, false);

    public static final int TYPE_EOF = 0;
    public static final int TYPE_NAME = 1;
    public static final int TYPE_KEYWORD = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_INT_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_FLOAT_DOUBLE = 6;
    public static final int TYPE_CHAR = 7;
    public static final int TYPE_STRING = 8;
    public static final String[] TYPES_NAME = {"EOF", "NAME", "KEYWORD", "INT", "INT_LONG", "FLOAT", "FLOAT_DOUBLE", "CHAR", "STRING"};

    private final String text;
    private final int type;

    // connNext or connPrev .?
    private final boolean isConnectedNext;

    private final int lineNumber;
    private final int charNumber;

    public Token(String s, int type, int linenum, int charnum, boolean isConnectedNext) {
        this.text = Objects.requireNonNull(s);
        this.type = type;
        this.lineNumber = linenum;
        this.charNumber = charnum;
        this.isConnectedNext = isConnectedNext;
    }

    public String text() {
        return text;
    }

    public int type() {
        return type;
    }

    public final int getLineNumber() {
        return lineNumber;
    }
    public final int getCharNumber() {
        return charNumber;
    }

    public final boolean isName() { return type == TYPE_NAME; }
    public final boolean isKeyword() { return type == TYPE_KEYWORD; }
    public final boolean isInt() { return type == TYPE_INT; }
    public final boolean isIntLong() { return type == TYPE_INT_LONG; }
    public final boolean isFloat() { return type == TYPE_FLOAT; }
    public final boolean isFloatDouble() { return type == TYPE_FLOAT_DOUBLE; }
    public final boolean isString() { return type == TYPE_STRING; }
    public final boolean isChar()   { return type == TYPE_CHAR; }


    @Override
    public String toString() {
        return "'"+text+"'";
    }

    public String detailString() {
        return String.format("\"%s\"(%s)[%s]", text, TYPES_NAME[type], locationString());
    }
    public String locationString() {
        return (lineNumber+1)+":"+(charNumber+1);
    }

    public boolean isConnectedNext() {
        return isConnectedNext;
    }


    public Token validate(Predicate<Token> pred) {
        if (!pred.test(this))
            throw new IllegalStateException();
        return this;
    }
}
