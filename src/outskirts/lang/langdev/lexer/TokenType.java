package outskirts.lang.langdev.lexer;

/**
 * Order still required at Predicting-Keyword. ("++" should before "+", or all "++" will be two "+")
 */
public enum TokenType {

    EOF("\0"),
    IDENTIFIER,
    LITERAL_INT,
    LITERAL_LONG,
    LITERAL_FLOAT,
    LITERAL_DOUBLE,
    LITERAL_CHAR,
    LITERAL_STRING,
    NEW("new"),
    SIZEOF("sizeof"),
    IS("is"),
//    DEREFERENCE("dereference"),
//    REFERENCE("reference"),
    LITERAL_TRUE("true"),
    LITERAL_FALSE("false"),
    // LITERAL_NULLPTR("nullptr"),
    // THIS("this"),  "this" should just simply a variable name.? instead of a keyword.

    CLASS("class"),
    NAMESPACE("namespace"),
    USING("using"),
    AS("as"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    RETURN("return"),
    BREAK("break"),
    CONTINUE("continue"),

    STATIC("static"),
    CONST("const"),

    AMPAMP("&&"),
    AMP("&"),
    BARBAR("||"),
    BAR("|"),

    EQEQ("=="),
    EQ("="),

    BANGEQ("!="),
    BANG("!"),

    PLUSPLUS("++"),
    PLUS("+"),
    SUBSUB("--"),
    ARROW("->"),
    SUB("-"),

    GTEQ(">="),
    GTGTGT(">>>"),
    GTGT(">>"),
    GT(">"),

    LTEQ("<="),
    LTLT("<<"),
    LT("<"),

    QUES("?"),
    CARET("^"),
    TILDE("~"),
    COLON(":"),
    DOT("."),
    AT("@"),
    COMMA(","),
    STAR("*"),
    SLASH("/"),
    SEMI(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}");

    public static final TokenType[] MODIFIERS = {
            STATIC, CONST
    };

    private String fixed;

    TokenType(String s) {
        fixed = s;
    }
    TokenType() { }

    public String fixed() {
        return fixed;
    }

    public static TokenType lookup(String keyword) {
        for (TokenType e : values()) {
            if (e.fixed != null && e.fixed.equals(keyword)) {
                return e;
            }
        }
        return null;
    }
}
