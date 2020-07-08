package outskirts.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class XML {

    private String name = "";
    private Map<String, String> attributes = new HashMap<>();
    private String textContent = "";
    private List<XML> children = new ArrayList<>();

    public XML() { }

    public XML(String s) {
        this(new Tokener(new StringReader(s)));
    }

    public XML(InputStream inputStream) {
        this(_fi_ignorePIsAndComments(new Tokener(new BufferedReader(new InputStreamReader(inputStream)))));
    }

    private XML(Tokener tk) {
        assert tk.nextClean() == '<';
        name = tk.nextIdentiter();

        while (true) {
            char c = tk.nextClean();
            if (c == '/') { // end tag. no child/content.
                assert tk.next() == '>';
                return;
            } else if (c == '>') { // done start-tag. read children.
                char c1 = tk.nextClean(); tk.back();
                if (c1 == '<') {  // read children or empty-content(directly </tag.)
                    while (true) {
                        assert tk.nextClean() == '<';
                        char c2 = tk.next();
                        if (c2 == '/') {  // end tag
                            assert tk.nextIdentiter().equals(name);
                            assert tk.next() == '>';
                            return;
                        } else if (c2 == '!') {
                            char c3 = tk.next();
                            if (c3 == '-') { // checks <!-- --> Comments
                                assert tk.next() == '-';
                                tk.nextTo("-->");
                                continue;
                            } else if (c3 == '[') { // read <![CDATA[ ... ]]>
                                assert tk.next(6).equals("CDATA[");
                                assert children.isEmpty();
                                textContent = tk.nextTo("]]>");
                                continue;
                            }
                        }
                        assert textContent.isEmpty();
                        tk.back(); // back of c2 (for test '/', '?')
                        tk.back(); //      of '<'
                        children.add(new XML(tk));
                    }
                } else { // read text-content
                    textContent = XML.unescape(tk.nextTo('<').trim());
                    assert tk.next() == '/';
                    assert tk.nextIdentiter().equals(name);
                    assert tk.next() == '>';
                    return;
                }
            } else { // read attribute
                tk.back();
                String attrKey = tk.nextIdentiter(":");
                assert tk.nextClean() == '=';
                char quote = tk.nextClean();
                assert quote=='\"' || quote=='\'';
                String attrValue = tk.nextTo(quote);
                attributes.put(attrKey, XML.unescape(attrValue));
            }
        }
    }

    private static Tokener _fi_ignorePIsAndComments(Tokener tk) {
        while (true) {
            assert tk.nextClean() == '<';
            char c1 = tk.next();

            if (c1 == '?') {  // checks <?xml ... ?>  Processing Instructions
                tk.nextTo('>');
            } else if (c1 == '!') {
                if (tk.next(2).equals("--")) {  // checks <!-- --> Comments
                    tk.nextTo("-->");
                } else {
                    throw new RuntimeException();
                }
            } else {
                tk.back();
                tk.back();
                return tk;
            }
        }
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    public final String getAttribute(String attrkey) {
        return getAttributes().get(attrkey);
    }

    public List<XML> getChildren() {
        assert !children.isEmpty() || !textContent.isEmpty();
        return children;
    }

    public String getTextContent() {
        return textContent;
    }
    public void setTextContent(String textContent) {
        assert children.isEmpty();
        this.textContent = textContent;
    }

    // get first child which name matched.
    public XML getChild(String name) {
        for (XML child : children) {
            if (child.getName().equals(name))
                return child;
        }
        return null;
    }

    public XML getChild(String name, String attrKey, String attrVal) {
        for (XML child : children) {
            if (child.getName().equals(name) && attrVal.equals(child.getAttributes().get(attrKey)))
                return child;
        }
        return null;
    }

    public List<XML> getChildren(String name) {
        List<XML> rs = new ArrayList<>();
        for (XML child : children) {
            if (child.getName().equals(name))
                rs.add(child);
        }
        return rs;
    }

    /**
     * recursive find children. (cross sub levels)
     * @param mx max find count. -1 unlimited.
     */
    private List<XML> findChildren(Predicate<XML> pred, List<XML> rs, int mx) {
        for (XML child : children) {
            if (mx != -1 && rs.size() == mx)
                return rs;
            if (pred.test(child)) {
                rs.add(child);
            }
            child.findChildren(pred, rs, mx);
        }
        return rs;
    }
    public final List<XML> findChildren(Predicate<XML> pred, int mx) {
        return findChildren(pred, new ArrayList<>(), mx);
    }





    @Override
    public String toString() {
        return toString(4, 0);
    }

    public final String toString(int indentFactor) {
        return toString(indentFactor, 0);
    }

    private String toString(int indentFactor, int indent) {
        if (!children.isEmpty() && !textContent.isEmpty())
            throw new RuntimeException();
        assert !name.isEmpty();

        StringBuilder sb = new StringBuilder();
        if (indentFactor > 0)
            sb.append(StringUtils.repeat(" ", indentFactor*indent));
        sb.append('<').append(name);
        for (Map.Entry<String, String> attr : attributes.entrySet())
            sb.append(' ').append(attr.getKey()).append('=').append('\"').append(XML.escape(attr.getValue())).append('\"');
        if (children.isEmpty() && textContent.isEmpty()) {
            sb.append("/>");
        } else {
            sb.append('>');
            if (!children.isEmpty()) {
                for (XML child : children) {
                    if (indentFactor > 0)
                        sb.append('\n');
                    sb.append(child.toString(indentFactor, indent+1));
                }
                if (indentFactor > 0)
                    sb.append('\n').append(StringUtils.repeat(" ", indentFactor*indent));
            } else {
                sb.append(XML.escape(textContent));
            }
            sb.append("</").append(name).append('>');
        }
        return sb.toString();
    }


    public static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length()); // init len. not actually output len.
        for (int i = 0;i < s.length();i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\"':
                    sb.append("&quot;"); break;
                case '\'':
                    sb.append("&apos;"); break;
                case '<':
                    sb.append("&lt;"); break;
                case '>':
                    sb.append("&gt;"); break;
                case '&':
                    sb.append("&amp;"); break;
                default:
                    if (ch < ' ' && ch != '\n' && ch != '\r') {
                        sb.append("&#x");
                        sb.append(Integer.toHexString(i));
                        sb.append(';');
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public static String unescape(String s) {
        if (s.indexOf('&') == -1)
            return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < s.length();i++) {
            char ch = s.charAt(i);
            if (ch == '&') {
                int ei = s.indexOf(';', i);
                if (ei != -1) {
                    String ent = s.substring(i+1, ei);
                    if (ent.charAt(0) == '#') {
                        if (ent.charAt(1) == 'x') {
                            sb.append(Integer.parseInt(ent.substring(2), 16));
                        } else {
                            sb.append(Integer.parseInt(ent.substring(1)));
                        }
                    } else {
                        switch (ent) {
                            case "quot":
                                sb.append('\"'); break;
                            case "apos":
                                sb.append('\''); break;
                            case "lt":
                                sb.append('<'); break;
                            case "gt":
                                sb.append('>'); break;
                            case "amp":
                                sb.append('&'); break;
                        }
                    }
                    i = ei;
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }


    private static class Tokener {

        private Reader reader;

        private int index;

        private int backSteps = -1;
        private char[] recentChars = new char[2];

        public Tokener(Reader reader) {
            this.reader = reader;

        }

        public void back() {
            if (index == 0 || backSteps >= recentChars.length)
                throw new RuntimeException();
            backSteps++;
            index--;
        }

        public char next() {
            index++;
            int c;
            if (backSteps != -1) {
                return recentChars[backSteps--];
            } else {
                try {
                    c = this.reader.read();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (c <= 0)  // EOF
                    return '\u0000';
                recentChars[1] = recentChars[0];
                recentChars[0] = (char)c;
                return recentChars[0];
            }
        }
        public String next(int count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0;i < count;i++) {
                char c = next();
                if (c == 0) throw new RuntimeException();
                sb.append(c);
            }
            return sb.toString();
        }

        public char nextClean() {
            char c;
            do {
                c = next();
            } while (c != 0 && c <= ' ');

            return c;
        }
        private static boolean isIdenChar(char c) {
            return (c >= 'A' && c <= 'Z' ) || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
        }

        public String nextIdentiter() {
            return nextIdentiter("");
        }
        public String nextIdentiter(String addallowedchars) {
            char c = next();
            if (!isIdenChar(c) || (c>='0' && c<='9'))
                throw new RuntimeException();
            StringBuilder sb = new StringBuilder().append(c);

            while (true) {
                c = next();
                if (isIdenChar(c) || addallowedchars.indexOf(c) != -1) {
                    sb.append(c);
                } else {
                    if (c != 0)
                        back();
                    break;
                }
            }
            return sb.toString();
        }

        public String nextTo(char ch) {
            StringBuilder sb = new StringBuilder();
            char c;
            while ((c=next()) != ch) {
                sb.append(c);
            }
            return sb.toString();
        }

        public String nextTo(String s) {
            StringBuilder sb = new StringBuilder();
            char fc = s.charAt(0);
            char c;
            while (true) {
                c = next();
                sb.append(c);
                if (c == fc) {
                    boolean mathced = true;
                    for (int i = 1;i < s.length();i++) {
                        c=next();
                        sb.append(c);
                        if (c != s.charAt(i)) {
                            mathced=false;
                            break;
                        }
                    }
                    if (mathced) {
                        return sb.substring(0, sb.length()-s.length());
                    }
                }
            }
        }

    }
}
