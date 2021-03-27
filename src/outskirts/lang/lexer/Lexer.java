package outskirts.lang.lexer;

import outskirts.util.Ref;
import outskirts.util.StringUtils;
import outskirts.util.Val;
import outskirts.util.Validate;
import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public int index;
    private List<Token> tokens = new ArrayList<>();

    public Lexer(final String code) {
        Val rfidx = Val.zero();
        char ch;
        while ((ch = nextUnblank(code, rfidx)) != 0) {
            String text;
            int type;
            final int idx = rfidx.i();  // tmp local.

            Val linenum = Val.zero(), charnum = Val.zero();
            StringUtils.locate(code, idx, linenum, charnum);

            if (startsWith("//", code, idx)) {   // singleline Comment.
                int end = code.indexOf("\n", idx);
                rfidx.val = end==-1 ? code.length() : end+1;  // +1: jump over the '\n'.
                continue;
            } else if (startsWith("/*", code, idx)) {  // multiline Comment.
                int end = code.indexOf("*/", idx);
                Validate.isTrue(end != -1, "Unterminaled Multiline Comment.");
                rfidx.val = end + 2;  // +2: jump over the "*/".
                continue;
            } else if (isInteger(ch) || (ch == '.' && code.length() > idx+1 && isInteger(code.charAt(idx+1)))) {  // Number Constant.

                text = readNumber(code, rfidx); type = Token.TYPE_NUMBER;
            } else if (ch == '"') {  // String Constant.

                text = readQuote(code, rfidx, '"'); type = Token.TYPE_STRING;
            } else if (isNameCharacter(ch, true)) {  // Name.

                text = readName(code, rfidx); type = Token.TYPE_NAME;
            } else if (isBorderCharacter(ch)) {  // Border.

                text = readBorder(code, rfidx); type = Token.TYPE_BORDER;
            } else {
                throw new IllegalStateException(String.format("Unexpected token: %s [%s]", ch, rfidx.val));
            }
            tokens.add(new Token(text, type, linenum.i(), charnum.i()));
        }
//        Log.LOGGER.info(tokens);
    }

    public Token peek() {
        if (index == tokens.size())
            return null;
        return tokens.get(index);
    }

    public Token next() {
        if (index == tokens.size())
            throw new IllegalStateException("EOF");
        return tokens.get(index++);
    }

    public List<Token> tokens() {
        return tokens;
    }

    public static char nextUnblank(String s, Val rfidx) {
        for (int i = (int)rfidx.val;i < s.length();i++) {
            char ch = s.charAt(i);
            if (ch > ' ') {
                rfidx.val = i;
                return ch;
            }
        }
        return 0;
    }

    private static boolean isBorderCharacter(char ch) {
        if (ch >= '!' && ch <= '/') return true;
        if (ch >= ':' && ch <= '@') return true;
        if (ch >= '[' && ch <= '`') return true;
        if (ch >= '{' && ch <= '~') return true;
        return false;
    }
    private static boolean isNameCharacter(char ch, boolean first) {
        if (ch == '_' || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
            return true;
        if (!first && isInteger(ch))
            return true;
        return false;
    }
    private static boolean isInteger(char ch) {
        return ch >= '0' && ch <= '9';
    }


    private static String readQuote(String s, Val rfidx, char quote) {
        StringBuilder sb = new StringBuilder();
        int start = (int)rfidx.val+1;
        for (int i = start;i < s.length();) {
            char ch = s.charAt(i);
            if (ch == quote) {
                i++;
                rfidx.val = i;
                return sb.toString();
            } else if (ch == '\\') {
                char nx = s.charAt(i+1);
                if (nx == '\'' || nx == '"') {
                    i += 2;
                    sb.append(nx);
                } else if (nx == 'u') {
                    String u4 = s.substring(i+2, i+6);
                    i += 6;
                    sb.append(StringUtils.fromHexString4(u4));
                }
            } else {
                i++;
                sb.append(ch);
            }
        }
        throw new IllegalStateException("unterminaled string.");
    }

    private static String readNumber(String s, Val rfidx) {
        StringBuilder sb = new StringBuilder();
        boolean p = false;
        int i = (int)rfidx.val;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (isInteger(c)) {
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
        rfidx.val = i;
        return sb.toString();
    }

    private static String readName(String s, Val rfidx) {
        StringBuilder sb = new StringBuilder();
        int start = (int)rfidx.val;
        int i = start;
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (isNameCharacter(ch, i==start)) {
                i++;
                sb.append(ch);
            } else {
                break;
            }
        }
        rfidx.val = i;
        return sb.toString();
    }

    private static String readBorder(String s, Val rfidx) {
        int i = (int)rfidx.val;
        if (startsWith("||",s,i) || startsWith("&&",s,i) ||
            startsWith("<<",s,i) || startsWith(">>",s,i) ||
            startsWith("==",s,i) || startsWith("!=",s,i) ||
            startsWith("<=",s,i) || startsWith(">=",s,i)) {
            rfidx.val = i+2;
            return s.substring(i, i+2);
        }
        if (isBorderCharacter(s.charAt(i))) {
            rfidx.val = i+1;
            return s.substring(i, i+1);
        }
        throw new IllegalStateException();
    }
    private static boolean startsWith(String search, String full, int fromIndex) {
        return full.indexOf(search, fromIndex) == fromIndex;
    }



}
