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

                if (startsWith("//", s, i)) {   // Singleline Comment.
                    int end = s.indexOf("\n", i);
                    idx.i = end == -1 ? s.length() : end + 1;  // +1: jump over the '\n'.
                    continue;
                } else if (startsWith("/*", s, i)) {  // Multiline Comment.
                    int end = s.indexOf("*/", i);
                    Validate.isTrue(end != -1, "Unterminated Multiline Comment.");
                    idx.i = end + 2;  // +2: jump over the "*/".
                    continue;
                } else if (isNumberChar(ch) || (ch == '.' && isNumberChar(atchar(s, i + 1)))) {  // Number Literal.
                    text = readNumber(s, idx);
                    type = Token.TYPE_NUMBER;
                } else if (ch == '"') {  // String Literal.
                    text = readQuote(s, idx, '"');
                    type = Token.TYPE_STRING;
                } else if (ch == '\'') {
                    text = readQuote(s, idx, '\'');
                    type = Token.TYPE_CHARACTER;
                    Validate.isTrue(text.length() == 1);
                } else if (isNameChar(ch, true)) {  // Name.
                    text = readName(s, idx);
                    type = Token.TYPE_NAME;
                } else if (isBorderChar(ch)) {  // Border.
                    text = readBorder(s, idx);
                    type = Token.TYPE_BORDER;
                } else {
                    throw new IllegalStateException(String.format("Unexpected token: '%s' in [%s:%s]", ch, nline.i, nchar.i));
                }

                boolean isNextToNext = !isBlankChar(atchar(s, idx.i));
                tokens.add(new Token(text, type, nline.i, nchar.i, isNextToNext));
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




    public final Lexer rqnext(String s) {
        Validate.isTrue(next().text().equals(s));
        return this;
    }


    public final boolean peeking(String connected) {
        return peekingc(connected) > 0;
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
            if (!isBlankChar(ch))
                return idx.i;
            idx.i++;
        }
        return -1;
    }
    private static boolean isBlankChar(char ch) {
        return ch <= ' ';
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

    private static boolean isNumberChar(char ch, int numtype) {
        switch (numtype) {
            case NUM_BINARY: return ch == '0' || ch == '1';
            case NUM_OCTAL: return ch >= '0' && ch <= '7';
            case NUM_DECIMAL: return ch >= '0' && ch <= '9';
            case NUM_HEX: return (ch>='0' && ch<='9') || (ch>='a' && ch<='f') || (ch>='A' && ch<='F');
            default: throw new IllegalArgumentException("Bad enum");
        }
    }
    private static boolean isNumberChar(char ch) {
        return isNumberChar(ch, NUM_DECIMAL);
    }

    public static final int NUM_BINARY  = 1;
    public static final int NUM_OCTAL   = 2;
    public static final int NUM_DECIMAL = 3;
    public static final int NUM_HEX     = 4;
    private static String readNumber(String s, Intptr idx) {
        StringBuilder sb = new StringBuilder();
        int i = idx.i;

        boolean dot = false;  // pointed.
        int numtype = NUM_DECIMAL;
        if (s.charAt(i) == '0') {  // Literal Num-Type Preditect
            char nx = atchar(s, i+1);

            if (nx=='x' || nx=='X') {  // Hex
                numtype = NUM_HEX;
                i += 2;
                sb.append('0');
                sb.append(nx);
            } else if (nx == 'b' || nx == 'B') {  // Binary
                numtype = NUM_BINARY;
                i += 2;
                sb.append('0');
                sb.append(nx);
            } else if (isNumberChar(nx, NUM_OCTAL)) {
                numtype = NUM_OCTAL;
                i++;
                sb.append('0');
            }

            if (numtype != NUM_DECIMAL) {
                Validate.isTrue(isNumberChar(atchar(s, i), numtype), "Bad number heading.");
            }
        }
        while (i < s.length()) {
            char c = s.charAt(i);
            if (isNumberChar(c, numtype)) {
                i++;
                sb.append(c);
            } else if (c == '.') {
                Validate.isTrue(!dot, "Decimal point already set.");
                Validate.isTrue(numtype == NUM_DECIMAL, "Hex number dosen't allowed decimal places.");
                i++;
                dot = true;
                sb.append(c);
            } else if (c == '_') {
                Validate.isTrue(isNumberChar(atchar(s,i-1), numtype) &&
                                     isNumberChar(atchar(s,i+1), numtype), "Neighber of _ must been nums.");
                i++;  // jus jumpover.
            } else {
                if (numtype != NUM_BINARY) {
                    if (c == 'e' || c == 'E') {  // fp exponent.
                        i++;
                        sb.append(c);
                        if (atchar(s, i) == '-') {
                            i++;
                            sb.append('-');
                        }
                        while (isNumberChar(c = s.charAt(i))) {
                            i++;
                            sb.append(c);
                        }
                    }
                    if (c == 'f' || c == 'F' ||
                            c == 'd' || c == 'D' ||
                            c == 'l' || c == 'L') {
                        i++;
                        sb.append(c);
                    }
                }
                break;
            }
        }
        idx.i = i;
        return sb.toString();
    }


    private static boolean isBorderChar(char ch) {
        if (ch >= '!' && ch <= '/') return true;
        if (ch >= ':' && ch <= '@') return true;
        if (ch >= '[' && ch <= '`') return true;
        if (ch >= '{' && ch <= '~') return true;
        return false;
    }
    public static final String[] BORDERS = {  // a.k.a. keywords.
            "++", "--",
            "&&", "||",
            "<<",            // ">>", ">>>",  the multiple ">" are not a single token anymore. since Typename<A<B>> syntax publish.
            "<=", ">=",
            "==", "!=",
            "=>"
    };

    private static String readBorder(String s, Intptr idx) {
        int i = idx.i;
        for (String bord : BORDERS) {
            if (startsWith(bord,s,i)) {
                int n = bord.length();
                idx.i += n;
                return s.substring(i, i+n);
            }
        }
        if (isBorderChar(s.charAt(i))) {
            idx.i += 1;
            return s.substring(i, i+1);
        }
        throw new IllegalStateException("Illegal border token.");
    }

    private static boolean startsWith(String search, String full, int full_FromIndex) {
        return full.indexOf(search, full_FromIndex) == full_FromIndex;
    }
    private static char atchar(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }



}
