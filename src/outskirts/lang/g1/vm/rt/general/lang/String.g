

class String {

    private final array<ushort> values;

    public ushort charAt(int idx) {
        return values.get(idx);
    }

    public int length() {
        return values.length;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public void getChars(...);


    public array<byte> getBytes(Charset charset);

    public boolean equalsIgnoreCase(String o);

    public boolean startsWith(String s, int fromIndex);

    public boolean endsWith(String s);

    public int indexOf(char ch, int fromIndex);  // ver.string

    public int lastIndexOf(char ch, int fromIndex);  // ver.string

    public String substring(int beginIndx, int endIndex);

    public boolean contains(String s);

    public String replace(String target, String replacement);

    public array<String> split(String s);

    public String toLowerCase();

    public String toUpperCase();

    public String trim();

    public array<ushort> toCharArray();

    public static String format(String s, Object... args);

    public static String valueOf(Object o);

    @Override
    public boolean equals(Object o) {

    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static String join(String delimiter, String... elements);
}