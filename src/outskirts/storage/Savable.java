package outskirts.storage;

import outskirts.storage.dst.DObject;

public interface Savable {

    void onRead(DObject mp);

    // maybe shoudn't uses "on" because the 'on' like a time-related event, but there are just 'Operations', not events.
    DObject onWrite(DObject mp);

    static Savable of(Object obj) {
        return null;
    }
}
