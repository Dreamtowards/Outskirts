package outskirts.lang.langdev.lexer;

import outskirts.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Objects;

/**
 * In-time Lexer.
 * reading token at in-time. not predict/parse all tokens at 'beginning'.
 * because some token may context-related, cant been predict at lexical-time.
 * and its not cost so much space-cost since not need holds all tokens.
 */
public final class Lexer {

//    private int index;
//    private final List<TokenItem> tokens = new ArrayList<>();
//    private final LinkedList<Integer> markers = new LinkedList<>();

//    private Token curr;

    public String sourceLocation = "file://SomeWhere/abc.n";
    private String srx = "";  // Source
    private int rdi;     // ReadIndex.
    private final LinkedList<Integer> rdimarkers = new LinkedList<>();  // for mark/setback.

    private final LinkedList<Integer> markedreadidxs = new LinkedList<>();  // for pushIdx, popIdx. AST SourceLocation QuickRangeDefine

    public String getSource() { return srx; }
    public void pushReadIdx() { markedreadidxs.push(rdi); }
    public int popReadIdx() { return markedreadidxs.pop(); }
    public int readidx() { return rdi; }

    // read/next/peek

    private Token read(TokenType expctedtype, boolean steppin) {

        Intptr idx = Intptr.of(rdi);
        _skipBlankAndComments(srx, idx);
        if (idx.i >= srx.length())
            return new Token(TokenType.EOF, null, new SourceLoc(null, srx, srx.length(), srx.length()));

        final int beg = idx.i;
        TokenType type = null;
        String content = null;  // only available for TType.fixed==null Types.

        if (expctedtype != null) {  // as expected.
            type = expctedtype;

            if (expctedtype == TokenType.IDENTIFIER) {
                content = readName(srx, idx);

                if (content == null)  // not a identifier
                    return null;
                if (TokenType.lookup(content) != null)  // overlapped with keyword.
                    return null;
            } else {  // keywords.
                String keyw = expctedtype.fixed();
                Validate.isTrue(keyw != null, "Unsupported dynmaic type.");

                if (!srx.startsWith(keyw, beg))  // not match.
                    return null;

                idx.i += keyw.length();
            }

        } else {  // as predicted.
            char ch = srx.charAt(beg);

            if (isNumberChar(ch) || (ch == '.' && beg + 1 < srx.length() && isNumberChar(srx.charAt(beg + 1)))) {
                Ref<TokenType> tmp = Ref.wrap();
                content = readNumber(srx, idx, tmp);
                type = tmp.value;
            } else if (ch == '\"') {
                content = readQuote(srx, idx, '\"');
                type = TokenType.LITERAL_STRING;
            } else if (ch == '\'') {
                content = readQuote(srx, idx, '\'');
                type = TokenType.LITERAL_CHAR;
            } else if (isNameChar(ch, true)) {  // before keywords. because a valid name may startsWith (but not equals) a keyword.
                content = readName(srx, idx);

                TokenType keywtyp = TokenType.lookup(content);
                if (keywtyp != null) {  // Overlapped with Keywords.
                    content = null;
                    type = keywtyp;
                } else {
                    type = TokenType.IDENTIFIER;
                }
            } else {
                for (TokenType e : TokenType.values()) {
                    String fixed = e.fixed();
                    if (fixed != null && srx.startsWith(fixed, beg)) {
                        type = e;
                        idx.i += fixed.length();
                        break;
                    }
                }
                Validate.isTrue(type != null, "Not found keyword at '"+ srx.substring(beg, Math.min(srx.length()-1, idx.i))+"'");
            }
        }

        if (steppin) {
            rdi = idx.i;
        }
        Validate.isTrue(type != null);
        Validate.isTrue(idx.i != beg, "nothing had been 'read'? ptr no change");
        return new Token(type, content, new SourceLoc(null, srx, beg, rdi));
    }

    private static void _skipBlankAndComments(String s, Intptr idx) {
        while (idx.i < s.length()) {
            int i = idx.i;

            i = skipBlanks(s, i);

            if (s.startsWith("//", i)) {
                int end = s.indexOf("\n", i+2);
                i = end==-1 ? s.length() : end+1;
            } else if (s.startsWith("/*", i)) {
                int end = s.indexOf("*/", i+2);
                Validate.isTrue(end!=-1, "Unterminated multiline comment.");
                i = end+2;
            }

            // no change anymore
            if (i == idx.i) {
                break;
            } else {
                idx.i = i;
            }
        }
    }

    public void appendsource(String src) {
        this.srx += src;
    }




    public Token peek() {
        return read(null, false);
    }
    public boolean peeking(TokenType expected) {
        Objects.requireNonNull(expected);
        return read(expected, false) != null;
    }
    public final boolean selpeeking(TokenType... expecteds) {
        for (TokenType e : expecteds) {
            if (peeking(e))
                return true;
        }
        return false;
    }

    @Nonnull
    public Token next(TokenType expected) {
        Token t = read(expected, true);
        if (t == null)
            throw new IllegalStateException("Expect token "+expected+", auto_detected: "+peek());
        return t;
    }
    public final Token next() {
        return next(null);
    }

    @Nullable
    public final Token trynext(TokenType expected) {
        Objects.requireNonNull(expected);
        return read(expected, true);
    }
    public final boolean nexting(TokenType expected) {
        return trynext(expected) != null;
    }

    @Nullable
    public final Token selnext(TokenType... expecteds) {
        Token t;
        for (TokenType e : expecteds) {
            if ((t = trynext(e)) != null)
                return t;
        }
        return null;
    }







    public void mark() {
        rdimarkers.push(rdi);
    }
    public void unmark() {
        rdi = rdimarkers.pop();
//        curr = null;  // curr is invalid since rdi changed.
    }
    public void cancelmark() {
        rdimarkers.pop();
    }
    public boolean isSpeculating() {
        return rdimarkers.size() > 0;
    }




//    public boolean peeking(TokenType t) {
//
//    }
//    public final Lexer match(String s) {
//        Token t = next();
//        Validate.isTrue(t.text().equals(s), "Bad token. expected: '"+s+"', actual: '"+t.text()+"'. at "+t.detailString());
//        return this;
//    }
//    public final boolean peeking(String connected) {
//        return peekingc(connected) > 0;
//    }
//    public final boolean peekingone(String... ors) {
//        for (String s : ors) {
//            if (peeking(s))
//                return true;
//        }
//        return false;
//    }
//
//    public final int peekingc(String connected) {  // "c" suffix, count of peeking connected
//
//        String ld = peek().text();
//        if (connected.equals(ld))       // quick optim
//            return 1;
//        if (!connected.startsWith(ld))  // quick optim
//            return 0;
//
//
//        int i = 1;              // token rel_idx offset
//        int off = ld.length();  // connected_str char offset.
//        while (off < connected.length()) {
//            Token t = tokens.get(index+(i++)); String c = t.text();
//            boolean leading = off + c.length() < connected.length();  // not last
//            if ((leading && !t.isConnectedNext()) ||
//                !connected.startsWith(c, off)) {
//                return 0;
//            }
//            off += c.length();
//        }
//        return i;
//    }
//    public final boolean peeking_skp(String s) {
//        int i;
//        if ((i= peekingc(s)) > 0) {
//            skip(i);
//            if (i > 1) {
//                System.out.println("Skipped "+i);
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//    public final String peekingone_skp(String... ls) {
//        for (String s : ls) {
//            if (peeking_skp(s))
//                return s;
//        }
//        return null;
//    }




    // deprecated way: char nextUnblank(), the 'next' is writable mean even for Unblanked chars.
    public static int skipBlanks(String s, int idx) {
        while (idx < s.length()) {
            char ch = s.charAt(idx);
            if (isUnblankChar(ch))
                return idx;
            idx++;
        }
        return idx;
    }
    private static boolean isUnblankChar(char ch) {
        return ch > ' ';
    }

    private static boolean isNameChar(char ch, boolean first) {
        return (ch=='_' || (ch>='A' && ch<='Z') || (ch>='a' && ch<='z'))
                || (!first && isNumberChar(ch));
    }
    private static String readName(String s, Intptr idxptr) {
        int i = idxptr.i;
        int begin = i;
        while (i < s.length()) {
            if (isNameChar(s.charAt(i), i==begin))
                i++;
            else
                break;
        }
        if (i == idxptr.i)
            return null;  // invalid name.
        idxptr.i = i;
        return s.substring(begin, i);
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

    public static final int NUML_BINARY = 1;  // Integer Number Literal Type. INLT.
    public static final int NUML_DECIMAL = 2;
    public static final int NUML_HEX = 3;
    private static String readNumber(String s, Intptr idx, Ref<TokenType> numtype_out) {
        int i = idx.i;
        int beg = i;

        boolean dot = false;  // fp. decimal point.
        int literaltype = NUML_DECIMAL;
        TokenType numtype = null;

        if (s.charAt(beg) == '0') {  // IntNumber LiteralType.
            char nx = atchar(s, beg+1);

            if (nx=='x' || nx=='X') {  // Hex
                literaltype = NUML_HEX;
                i += 2;
            } else if (nx == 'b' || nx == 'B') {  // Binary
                literaltype = NUML_BINARY;
                i += 2;
            } else {  // Decimal validate no 0 leading.
                Validate.isTrue(!isNumberChar(nx), "decimal integer 0-leading is not allowed.");  // 0123 is not allowed. confuse with some octal form.
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
                numtype = TokenType.LITERAL_DOUBLE;
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
                        numtype = TokenType.LITERAL_DOUBLE;
                    }
                    // Suffixes.
                    if (c == 'f' || c == 'F') {
                        i++;
                        numtype = TokenType.LITERAL_FLOAT;
                    } else if (c == 'd' || c == 'D') {
                        i++;
                        numtype = TokenType.LITERAL_DOUBLE;
                    } else if (c == 'l' || c == 'L') {
                        Validate.isTrue(numtype == null);
                        i++;
                        numtype = TokenType.LITERAL_LONG;
                    } else {
                        numtype = TokenType.LITERAL_INT;
                    }
                }
                break;
            }
        }
        idx.i = i;
        numtype_out.value = Objects.requireNonNull(numtype);
        return s.substring(beg, i);
    }

    private static char atchar(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }



}
