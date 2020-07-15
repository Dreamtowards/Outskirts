package outskirts.storage;

import java.util.Map;

public interface Savable {

    void onRead(DataMap mp);

    // maybe shoudn't uses "on" because the 'on' like a time-related event, but there are just 'Operations', not events.
    Map onWrite(DataMap mp);
}
