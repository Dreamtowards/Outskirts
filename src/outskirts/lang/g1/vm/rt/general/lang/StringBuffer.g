package general.lang;


class StringBuffer {

    char[] value;


    public StringBuffer append(Object o);

    public StringBuffer append(String s);

    public StringBuffer remove(int begin, int end);

    public StringBuffer insert(int index, String s);

    @Override
    public String toString();

}