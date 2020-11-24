package outskirts.storage;

import outskirts.storage.dat.DATObject;

import java.util.Map;

public interface Savable {

    void onRead(DATObject mp);

    // maybe shoudn't uses "on" because the 'on' like a time-related event, but there are just 'Operations', not events.
    DATObject onWrite(DATObject mp);
}
