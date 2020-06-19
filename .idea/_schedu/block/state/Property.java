package outskirts.block.state;

public final class Property {

    private final String name;
    private final int capacity;

    public Property(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public String name() {
        return name;
    }
}
