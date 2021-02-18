package outskirts.storage;

import outskirts.storage.dst.DObject;

import java.io.IOException;

public interface Savable {

    void onRead(DObject mp) throws IOException;

    // maybe shoudn't uses "on" because the 'on' like a time-related event, but there are just 'Operations', not events.
    DObject onWrite(DObject mp) throws IOException;

    static Savable of(Object obj) {
        return null;
    }
}
