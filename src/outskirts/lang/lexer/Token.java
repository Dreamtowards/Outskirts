package outskirts.lang.lexer;

public final class Token {

    public static final int TYPE_NAME = 1;  // Identifier
    public static final int TYPE_BORDER = 2;
    public static final int TYPE_NUMBER = 3;
    public static final int TYPE_STRING = 4;
    public static final String[] TYPES_NAME = {"NULL", "NAME", "BORDER", "NUMBER", "STRING"};

    private final String text;
    private final int type;

    private final int lineNumber;
    private final int charNumber;

    public Token(String s, int type, int linenum, int charnum) {
        this.text = s;
        this.type = type;
        this.lineNumber = linenum;
        this.charNumber = charnum;
    }

    public String text() {
        return text;
    }

    public int type() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    public int getCharNumber() {
        return charNumber;
    }

    public final boolean isName() {
        return type == TYPE_NAME;
    }
    public final boolean isBorder() {
        return type == TYPE_BORDER;
    }
    public final boolean isNumber() {
        return type == TYPE_NUMBER;
    }
    public final boolean isString() {
        return type == TYPE_STRING;
    }

    @Override
    public String toString() {
        return text;
    }

    public String detailString() {
        return String.format("\"%s\"[%s](%s:%s)", text, TYPES_NAME[type], lineNumber+1, charNumber+1);
    }
}
