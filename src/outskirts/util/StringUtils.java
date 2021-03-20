package outskirts.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class StringUtils {

    public static final String CR = "\r"; //CarriageReturn
    public static final String LF = "\n"; //LineFeed
    public static final String CR_LF = "\r\n";
    public static final String EMPTY = "";
    public static final String SPACE = " ";

    private static final String HEX_MAPPING = "0123456789abcdef";

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEX_MAPPING.charAt((b >> 4) & 0xF));
            sb.append(HEX_MAPPING.charAt(b & 0xF));
        }
        return sb.toString();
    }

    public static byte[] fromHexString(String hex) {
        Validate.isTrue(hex.length() % 2 == 0);
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0;i < b.length;i++) {
            int base = i*2;
            int n1 = HEX_MAPPING.indexOf(hex.charAt(base)),
                n2 = HEX_MAPPING.indexOf(hex.charAt(base+1));
            Validate.isTrue(n1 != -1 && n2 != -1, "illegal hex.");
            b[i] = (byte)((n1 << 4) | n2);
        }
        return b;
    }
    public static char fromHexString4(String hex) {
        Validate.isTrue(hex.length() == 4, "required 4 hexcode.");
        byte[] b = fromHexString(hex);
        return (char)(((b[0] << 8) & 0xF000) | ((b[0] << 8) & 0xF00) | (b[1] & 0xF0) | (b[1] & 0xF));
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




    public static boolean isInteger(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * -1.32e-8
     * @return a range. the number String is s.substring(r[0], r[1]).
     */
    public static int[] nextNumber(CharSequence s, int fromIndex) {  // Pattern.compile("-?\\d+(\\.\\d+)?([eE]-?\\d+)?");
        int startm = -1;  // matching start
        for (int i = fromIndex;i < s.length();i++) {
            char ch = s.charAt(i);
            if (startm == -1) {
                if (StringUtils.isInteger(ch)) {
                    startm=i;
                } else if (ch == '-') {
                    if (i+1 < s.length() && StringUtils.isInteger(s.charAt(i+1)))
                        startm=i;
                }
            } else {
                if (ch == '.') {
                    if (i == s.length()-1 || !StringUtils.isInteger(s.charAt(i+1)))
                        return new int[] {startm, i};
                } else if (ch == 'e' || ch == 'E') {
                    if (i==s.length()-1 || ("+-".indexOf(s.charAt(i+1)) == -1 && !StringUtils.isInteger(s.charAt(i+1)))) {
                        return new int[] {startm, i};
                    } else { // is Num
                        i++; // step over [eE]
                        if ("+-".indexOf(s.charAt(i)) != -1) {
                            i++;
                            if (i==s.length() || !StringUtils.isInteger(s.charAt(i)))
                                return new int[] {startm, i-2};
                        }
                        while (i < s.length()) {
                            if (!StringUtils.isInteger(s.charAt(i)))
                                return new int[] {startm, i};
                            i++;
                        }
                    }
                } else if (!StringUtils.isInteger(ch)) {
                    return new int[] {startm, i};
                }
            }
        }
        if (startm != -1) {
            return new int[] {startm, s.length()};
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


    /**
     * compare version string
     * "1.0.0", "1.0.1":    -1
     * "1.1.0", "1.0.100":   1
     * "2",     "1.100.100": 1
     * "1",     "1.0.0":     0
     * @return 1: v1 bigger than v2.  0: v1 equals v2.  -1:  v1 litter than v2.
     */
    public static int compv(String vstr1, String vstr2) {
        String[] v1cps = StringUtils.explode(vstr1, ".");
        String[] v2cps = StringUtils.explode(vstr2, ".");
        for (int i = 0;i < Math.max(v1cps.length, v2cps.length);i++) {
            int s = (int)Math.signum((i<v1cps.length?Integer.parseInt(v1cps[i]):0) - (i<v2cps.length?Integer.parseInt(v2cps[i]):0));
            if (s > 0) return 1;
            if (s < 0) return -1;
        }
        return 0;
    }

    public static int count(String s, String search) {
        int num = 0, idx = 0;
        while (true) {
            int i = s.indexOf(search, idx);
            if (i == -1) break;
            idx = i+search.length();
            num++;
        }
        return num;
    }
}
