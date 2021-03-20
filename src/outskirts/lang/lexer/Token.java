package outskirts.lang.lexer;

public class Token {

    public static final int TYPE_NAME = 1;  // Identifier
    public static final int TYPE_BORDER = 2;
    public static final int TYPE_NUMBER = 3;
    public static final int TYPE_STRING = 4;

    private String text;
    private int type;

    private int lineNumber;
    private int charNumber;

    public Token(String s, int type) {
        this.text = s;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int type() {
        return type;
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
}
