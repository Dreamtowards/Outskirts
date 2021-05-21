package outskirts.lang.g1.interpreter;

public class ObjectInstance {

    public String type;
    public Object value;

    public ObjectInstance(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "INST'"+type+"'{"+value+"}";
    }
}
