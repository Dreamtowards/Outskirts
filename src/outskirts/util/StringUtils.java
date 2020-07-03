package outskirts.util;

import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class StringUtils {

    public static final String CR = "\r"; //CarriageReturn
    public static final String LF = "\n"; //LineFeed
    public static final String CR_LF = "\r\n";
    public static final String EMPTY = "";
    public static final String SPACE = " ";

    private static final char[] HEX_MAPPING = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEX_MAPPING[(b >> 4) & 0x0F]);
            sb.append(HEX_MAPPING[b & 0x0F]);
        }
        return sb.toString();
    }

    /**
     * str:"a = b =c", sep:"="   -> ["a ", " b ", "c"]
     * str:"a = b =c", sep:"b"   -> ["a = ", " =c"]
     * str:"a = b =c", sep:"ABC" -> ["a = b =c"]
     * str:"="       , sep:"="   -> ["", ""]
     */
    public static String[] explode(String str, String separator) {
        List<String> result = new LinkedList<>();
        int pointer = 0;
        int next;
        while ((next = str.indexOf(separator, pointer)) != -1) {
            result.add(str.substring(pointer, next));
            pointer = next + separator.length();
        }
        result.add(str.substring(pointer));
        return result.toArray(new String[0]);
    }

    public static String[] explodeSpaces(String str) {
        List<String> result = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < str.length();i++) {
            char ch = str.charAt(i);
            if (ch == ' ') {
                if (sb.length() != 0) {
                    result.add(sb.toString());
                    sb.delete(0, sb.length()); // sb.clear();
                }
            } else {
                sb.append(ch);
            }
        }
        if (sb.length() != 0) {
            result.add(sb.toString());
        }
        return result.toArray(new String[0]);
    }

    public static String replaceLast(String str, String target, String replacement) {
        int pos = str.lastIndexOf(target);
        if (pos == -1) return str;

        return str.substring(0, pos) + replacement + str.substring(pos + target.length());
    }

    public static String repeat(String c, int amount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < amount;i++)
            sb.append(c);
        return sb.toString();
    }




    public static boolean isNumrical(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static int[] nextNumber(CharSequence s, int fromIndex) {  // Pattern.compile("-?\\d+(\\.\\d+)?");
        int matchingStart = -1;
        for (int i = fromIndex;i < s.length();i++) {
            char ch = s.charAt(i);
            if (matchingStart == -1) {
                if (StringUtils.isNumrical(ch)) {
                    matchingStart=i;
                } else if (ch == '-') {
                    if (i+1 < s.length() && StringUtils.isNumrical(s.charAt(i+1)))
                        matchingStart=i;
                }
            } else {
                if (ch == '.') {
                    if (i+1 == s.length() || !StringUtils.isNumrical(s.charAt(i+1)))
                        return new int[] {matchingStart, i};
                } else if (!StringUtils.isNumrical(ch)) {
                    return new int[] {matchingStart, i};
                }
            }
        }
        if (matchingStart != -1) {
            return new int[] {matchingStart, s.length()};
        }
        return null;
    }

    public static float[] readNumbers(String s, float[] dest) {
        if (dest == null) {
            List<String> ls = new ArrayList<>();
            int i = 0;
            while (true) {
                int[] r = StringUtils.nextNumber(s, i);
                if (r==null) break;
                i=r[1];
                ls.add(s.substring(r[0], r[1]));
            }
            dest = new float[ls.size()];
            for (int k = 0;k < dest.length;k++) {
                dest[k] = Float.parseFloat(ls.get(k));
            }
            return dest;
        }
        int i = 0;
        for (int j = 0;j < dest.length;j++) {
            int[] r = StringUtils.nextNumber(s, i);
            if (r==null)
                throw new IllegalArgumentException();
            i = r[1];
            dest[j] = Float.parseFloat(s.substring(r[0], r[1]));
        }
        return dest;
    }
}
