package outskirts.storage;

import outskirts.storage.dst.DObject;

public abstract class Saver<T> {

    public abstract void read(T obj, DObject mp);

    public abstract DObject write(T obj, DObject mp);

}
