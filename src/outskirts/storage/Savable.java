package outskirts.storage;

import java.util.Map;

public interface Savable {

    void onRead(Map mp);

    void onWrite(Map mp);

    default Map onWriteG(Map mp) {
        onWrite(mp);
        return mp;
    }
}
