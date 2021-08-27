package outskirts.lang.langdev.lexer;

import outskirts.util.*;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    public int index;
    private final List<Token> tokens = new ArrayList<>();

    public void read(final String s) {

        Intptr idx = Intptr.zero();
        Intptr nline = Intptr.zero(), nchar = Intptr.zero();

        try {
            while (skipBlanks(s, idx) != -1) {
                final int i = idx.i;  // tmp local.
                final char ch = s.charAt(idx.i);

                String text;
                int type;

                StringUtils.locate(s, i, nline, nchar);

                if (s.startsWith("//", i))
                {   // Singleline Comment.
                    int end = s.indexOf("\n", i);
                    idx.i = end == -1 ? s.length() : end + 1;  // +1: jump over the '\n'.
                    continue;
                }
                else if (s.startsWith("/*", i))
                {  // Multiline Comment.
                    int end = s.indexOf("*/", i);
                    Validate.isTrue(end != -1, "Unterminated Multiline Comment.");
                    idx.i = end + 2;  // +2: jump over the "*/".
                    continue;
                }
                else if (isNumberChar(ch) || (ch == '.' && isNumberChar(atchar(s, i + 1))))    // Number Literal.
                {
                    Ref<Integer> numtype_out = Ref.wrap();

                    text = readNumber(s, idx, numtype_out);
                    type = numtype_out.value;
                }
                else if (ch == '"')   // String Literal.
                {
                    text = readQuote(s, idx, '"');
                    type = Token.TYPE_STRING;
                }
                else if (ch == '\'')   // Char Literal.
                {
                    text = readQuote(s, idx, '\'');
                    type = Token.TYPE_CHAR;
                    Validate.isTrue(text.length() == 1);
                }
                else if ((text = lookupKeyword(s, i)) != null)   // Keyword. Border.
                {
                    idx.i += text.length();
                    type = Token.TYPE_KEYWORD;
                }
                else if (isNameChar(ch, true))
                {  // Name.
                    text = readName(s, idx);
                    type = Token.TYPE_NAME;
                }
                else
                {
                    throw new IllegalStateException(String.format("Unexpected token: '%s' in [%s:%s]", ch, nline.i, nchar.i));
                }

                boolean isConnectedNext = isUnblankChar(atchar(s, idx.i));
                tokens.add(new Token(text, type, nline.i, nchar.i, isConnectedNext));
            }


//            StringUtils.locate(s, s.length(), nline, nchar);
//            tokens.add(new Token(Token.EOF_T, Token.TYPE_EOF, nline.i, nchar.i, false));
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("Lexer reading error. at '%s' in [%s:%s]", s.charAt(idx.i), nline.i, nchar.i), ex);
        }
    }

    public Token peek() {
        if (eof())
            return Token.EOF;
        return tokens.get(index);
    }
    public Token next() {
        if (eof())
            return Token.EOF;
        return tokens.get(index++);
    }

    public void skip(int i) {
        index += i;
    }
    public void skip() {
        skip(1);
    }
    public void back() {
        skip(-1);
    }

    public boolean eof() {
        return index == tokens.size();
    }

    public List<Token> tokens() {
        return tokens;
    }




    public final Lexer match(String s) {
        Token t = next();
        Validate.isTrue(t.text().equals(s), "Bad token. expected: '"+s+"', actual: '"+t.text()+"'. at "+t.detailString());
        return this;
    }


    public final boolean peeking(String connected) {
        return peekingc(connected) > 0;
    }
    public final boolean peekingone(String... ors) {
        for (String s : ors) {
            if (peeking(s))
                return true;
        }
        return false;
    }

    public final int peekingc(String connected) {  // "c" suffix, count of peeking connected

        String ld = peek().text();
        if (connected.equals(ld))       // quick optim
            return 1;
        if (!connected.startsWith(ld))  // quick optim
            return 0;


        int i = 1;              // token rel_idx offset
        int off = ld.length();  // connected_str char offset.
        while (off < connected.length()) {
            Token t = tokens.get(index+(i++)); String c = t.text();
            boolean leading = off + c.length() < connected.length();  // not last
            if ((leading && !t.isConnectedNext()) ||
                !connected.startsWith(c, off)) {
                return 0;
            }
            off += c.length();
        }
        return i;
    }


    public final boolean peeking_skp(String s) {
        int i;
        if ((i= peekingc(s)) > 0) {
            skip(i);
            if (i > 1) {
                System.out.println("Skipped "+i);
            }
            return true;
        } else {
            return false;
        }
    }
    public final String peekingone_skp(String... ls) {
        for (String s : ls) {
            if (peeking_skp(s))
                return s;
        }
        return null;
    }




    // deprecated way: char nextUnblank(), the 'next' is writable mean even for Unblanked chars.
    public static int skipBlanks(String s, Intptr idx) {
        while (idx.i < s.length()) {
            char ch = s.charAt(idx.i);
            if (isUnblankChar(ch))
                return idx.i;
            idx.i++;
        }
        return -1;
    }
    private static boolean isUnblankChar(char ch) {
        return ch > ' ';
    }

    private static boolean isNameChar(char ch, boolean first) {
        return (ch=='_' || (ch>='A' && ch<='Z') || (ch>='a' && ch<='z'))
                || (!first && isNumberChar(ch));
    }
    private static String readName(String s, Intptr idx) {
        StringBuilder sb = new StringBuilder();
        int begin = idx.i;
        while (idx.i < s.length()) {
            char ch = s.charAt(idx.i);
            if (isNameChar(ch, idx.i==begin)) {
                sb.append(ch);
                idx.i++;
            } else {
                break;
            }
        }
        return sb.toString();
    }


    private static String readQuote(String s, Intptr idx, char quote) {
        Validate.isTrue(s.charAt(idx.i) == quote);
        StringBuilder sb = new StringBuilder();
        int i = idx.i +1;
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (ch == quote) {
                i++;
                idx.i = i;
                return sb.toString();
            } else if (ch == '\\') {
                char ch1 = s.charAt(i+1);
                if (ch1=='\'' || ch1=='"') {
                    sb.append(ch1);
                    i += 2;
                } else if (ch1 == 'u') {
                    String u4 = s.substring(i+2, i+6);
                    sb.append(StringUtils.fromHexString4(u4));
                    i += 6;
                } else {
                    throw new IllegalStateException("Illegal escape: '"+ch1+"'");
                }
            } else {
                sb.append(ch);
                i++;
            }
        }
        throw new IllegalStateException("Unterminated string.");
    }

    private static boolean isNumberChar(char ch, int literaltype) {
        switch (literaltype) {
            case NUML_BINARY: return ch == '0' || ch == '1';
            case NUML_DECIMAL: return ch >= '0' && ch <= '9';
            case NUML_HEX: return (ch>='0' && ch<='9') || (ch>='a' && ch<='f') || (ch>='A' && ch<='F');
            default: throw new IllegalArgumentException("Bad enum");
        }
    }
    private static boolean isNumberChar(char ch) {
        return isNumberChar(ch, NUML_DECIMAL);
    }

    public static final int NUML_BINARY = 1;
    public static final int NUML_DECIMAL = 2;
    public static final int NUML_HEX = 3;
    private static String readNumber(String s, Intptr idx, Ref<Integer> numtype_out) {
        int begin = idx.i;
        int i = idx.i;

        boolean dot = false;  // fp. decimal point.
        int literaltype = NUML_DECIMAL;
        int numtype = Token.TYPE_INT;

        // Number Literal Type Define.
        if (s.charAt(begin) == '0') {
            char nx = atchar(s, begin+1);
            if (nx=='x' || nx=='X') {  // Hex
                literaltype = NUML_HEX;
                i += 2;
            } else if (nx == 'b' || nx == 'B') {  // Binary
                literaltype = NUML_BINARY;
                i += 2;
            } else {  // Decimal validate.
                Validate.isTrue(!isNumberChar(nx), "0-leading decimal integer is not allowed.");  // 0123 is not allowed. confuse with some octal form.
            }

            if (literaltype != NUML_DECIMAL) {
                Validate.isTrue(isNumberChar(s.charAt(i), literaltype), "Bad number heading.");
            }
        }

        while (i < s.length()) {
            char c = s.charAt(i);
            if (isNumberChar(c, literaltype)) {
                i++;
            } else if (c == '.') {
                Validate.isTrue(!dot, "Decimal point already set.");
                Validate.isTrue(literaltype == NUML_DECIMAL, "Decimal places only allowed for Decimals.");
                i++;
                dot = true;
                numtype = Token.TYPE_FLOAT_DOUBLE;
            } else if (c == '_') {
                Validate.isTrue(isNumberChar(atchar(s,i-1), literaltype) &&
                                     isNumberChar(atchar(s,i+1), literaltype), "Neighber of _ must be nums.");
                i++;
            } else {
                if (literaltype == NUML_DECIMAL) {
                    if (c == 'e' || c == 'E') {  // fp exponent.
                        i++;
                        if (atchar(s, i) == '-') {
                            i++;
                        }
                        while (isNumberChar(s.charAt(i))) {
                            i++;
                        }
                        numtype = Token.TYPE_FLOAT_DOUBLE;
                    }
                    // Suffixes.
                    if (c == 'f' || c == 'F') {
                        i++;
                        numtype = Token.TYPE_FLOAT;
                    } else if (c == 'd' || c == 'D') {
                        i++;
                        numtype = Token.TYPE_FLOAT_DOUBLE;
                    } else if (c == 'l' || c == 'L') {
                        i++;
                        numtype = Token.TYPE_INT_LONG;
                    }
                }
                break;
            }
        }
        idx.i = i;
        numtype_out.value = numtype;
        return s.substring(begin, i);
    }


    public static final String[] KEYWORDS = {  // a.k.a. keywords.
            "++", "--",
            "&&", "||",
            "<<",            // ">>", ">>>",  the multiple ">" are not a single token anymore. since Typename<A<B>> syntax publish.
            "<=", ">=",
            "==", "!=",
            "=>",
            "new",
            ";", "!", "=", ".", "+", "-", "*", "/", "{", "}", "(", ")",
            "@"
    };
    private static String lookupKeyword(String s, int i) {
        for (String k : KEYWORDS) {
            if (s.startsWith(k, i)) {
                return k;
            }
        }
        return null;
    }

    private static char atchar(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }



}
