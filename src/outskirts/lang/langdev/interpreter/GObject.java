package outskirts.lang.langdev.interpreter;

public class GObject {

    public static final GObject VOID = new GObject(null);

    /**
     * general.lang.int
     *
     */
    public String type;

    public Object value;

    public GObject(String type, Object value) {
        this.type = type;
        this.value = value;
    }
    public GObject(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GObject{"+type+";"+value+"}";
    }


    public static GObject ofBoolean(boolean b) {
        return new GObject(b ? 1 : 0);
    }
}
