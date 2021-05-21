package outskirts.lang.langdev.lexer;

import outskirts.util.*;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    public int index;
    private final List<Token> tokens = new ArrayList<>();

    public void read(final String s) {
        Intptr idx = Intptr.zero();
        while (skipBlanks(s, idx) != -1) {
            final int i = idx.i;  // tmp local.
            final char ch = s.charAt(idx.i);

            String text; int type;

            Intptr nline = Intptr.zero(), nchar = Intptr.zero();
            StringUtils.locate(s, i, nline, nchar);

            if (startsWith("//", s,i)) {   // Singleline Comment.
                int end = s.indexOf("\n", i);
                idx.i = end==-1 ? s.length() : end+1;  // +1: jump over the '\n'.
                continue;
            } else if (startsWith("/*", s,i)) {  // Multiline Comment.
                int end = s.indexOf("*/", i);
                Validate.isTrue(end != -1, "Unterminated Multiline Comment.");
                idx.i= end +2;  // +2: jump over the "*/".
                continue;
            } else if (isIntegerChar(ch) || (ch=='.' && isIntegerChar(atchar(s,i+1)))) {  // Number Literal.
                text = readNumber(s, idx);
                type = Token.TYPE_NUMBER;
            } else if (ch == '"') {  // String Literal.
                text = readQuote(s, idx, '"');
                type = Token.TYPE_STRING;
            } else if (isNameChar(ch, true)) {  // Name.
                text = readName(s, idx);
                type = Token.TYPE_NAME;
            } else if (isBorderChar(ch)) {  // Border.
                text = readBorder(s, idx);
                type = Token.TYPE_BORDER;
            } else {
                throw new IllegalStateException(String.format("Unexpected token: %s in [%s:%s]", ch, nline.i, nchar.i));
            }

            tokens.add(new Token(text, type, nline.i, nchar.i));
        }
    }

    public Token peek() {
        if (eof())
            return Token.EOF;
            // throw new IllegalStateException("EOF");
        return tokens.get(index);
    }

    public Token next() {
        if (eof())
            return Token.EOF;
            // throw new IllegalStateException("EOF");
        return tokens.get(index++);
    }

    public boolean eof() {
        return index == tokens.size();
    }

    public List<Token> tokens() {
        return tokens;
    }

    // deprecated way: char nextUnblank(), the 'next' is writable mean even for Unblanked chars.
    public static int skipBlanks(String s, Intptr idx) {
        while (idx.i < s.length()) {
            char ch = s.charAt(idx.i);
            if (ch > ' ')
                return idx.i;
            idx.i++;
        }
        return -1;
    }

    private static boolean isBorderChar(char ch) {
        if (ch >= '!' && ch <= '/') return true;
        if (ch >= ':' && ch <= '@') return true;
        if (ch >= '[' && ch <= '`') return true;
        if (ch >= '{' && ch <= '~') return true;
        return false;
    }
    private static boolean isNameChar(char ch, boolean first) {
        return (ch=='_' || (ch>='A' && ch<='Z') || (ch>='a' && ch<='z'))
                || (!first && isIntegerChar(ch));
    }
    private static boolean isIntegerChar(char ch) {
        return ch >= '0' && ch <= '9';
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

    private static String readNumber(String s, Intptr idx) {
        StringBuilder sb = new StringBuilder();
        boolean p = false;  // pointed.
        int i = idx.i;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (isIntegerChar(c)) {
                i++;
                sb.append(c);
            } else if (!p && c == '.') {
                i++;
                p = true;
                sb.append(c);
            } else if (i != 0 && c == '_') {
                i++;
            } else {
                if (c == 'f' || c == 'd') {
                    i++;
                    sb.append(c);
                }
                break;
            }
        }
        idx.i = i;
        return sb.toString();
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

    private static String readBorder(String s, Intptr idx) {
        int i = idx.i;
        if (startsWith("||",s,i) || startsWith("&&",s,i) ||
            startsWith("<<",s,i) || startsWith(">>",s,i) ||
            startsWith("==",s,i) || startsWith("!=",s,i) ||
            startsWith("<=",s,i) || startsWith(">=",s,i)) {
            idx.i += 2;
            return s.substring(i, i+2);
        }
        if (isBorderChar(s.charAt(i))) {
            idx.i += 1;
            return s.substring(i, i+1);
        }
        throw new IllegalStateException("Illegal border token.");
    }

    private static boolean startsWith(String search, String full, int fromIndex) {
        return full.indexOf(search, fromIndex) == fromIndex;
    }
    private static char atchar(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }



}
