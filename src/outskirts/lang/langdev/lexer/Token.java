package outskirts.lang.langdev.lexer;

public final class Token {

//    public static Token EOF = new Token("", 0, 0, 0);

    public static final int TYPE_NAME = 1;
    public static final int TYPE_BORDER = 2;
    public static final int TYPE_NUMBER = 3;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_CHARACTER = 5;
    public static final String[] TYPES_NAME = {"NULL", "NAME", "BORDER", "NUMBER", "STRING", "CHARACTER"};

    private final String text;
    private final int type;

    // connNext or connPrev .?
    private final boolean isNextToTheNext;

    private final int lineNumber;
    private final int charNumber;

    public Token(String s, int type, int linenum, int charnum, boolean isNextToTheNext) {
        this.text = s;
        this.type = type;
        this.lineNumber = linenum;
        this.charNumber = charnum;
        this.isNextToTheNext = isNextToTheNext;
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

    public final boolean isName() {
        return type == TYPE_NAME;
    }
    public final boolean isBorder() {
        return type == TYPE_BORDER;
    }
    public final boolean isIdentifier() {
        return isName() || isBorder();
    }
    public final boolean isNumber() {
        return type == TYPE_NUMBER;
    }
    public final boolean isString() {
        return type == TYPE_STRING;
    }

    @Override
    public String toString() {
        return text+(isNextToTheNext?"[Y]":"[N]");
    }

    public String detailString() {
        return String.format("\"%s\"(%s)[%s]", text, TYPES_NAME[type], locationString());
    }
    public String locationString() {
        return (lineNumber+1)+":"+(charNumber+1);
    }

    public boolean isNextToTheNext() {
        return isNextToTheNext;
    }
}
