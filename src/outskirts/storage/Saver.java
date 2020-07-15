package outskirts.storage;

public abstract class Saver<T> {

    public abstract void read(T obj, DataMap mp);

    public abstract DataMap write(T obj, DataMap mp);

}
