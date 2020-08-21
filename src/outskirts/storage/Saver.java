package outskirts.storage;

import outskirts.storage.dat.DATObject;

public abstract class Saver<T> {

    public abstract void read(T obj, DATObject mp);

    public abstract DATObject write(T obj, DATObject mp);

}
