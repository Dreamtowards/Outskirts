package outskirts.storage;

import outskirts.storage.dst.DObject;
import outskirts.util.ReflectionUtils;

import java.io.IOException;

// Savable: use for the Applicate-Layer,
// dont use to core-layer. thats'll drag down the Wide. bring chaos.
public interface Savable {

    // throws IOException: sometimes needs some byte[],IO-stream operations.

    // return this;.?
    void onRead(DObject mp) throws IOException;

    // maybe shoudn't uses "on" because the 'on' like a time-related event, but there are just 'Operations', not events.
    DObject onWrite(DObject mp) throws IOException;

    static Savable of(Object obj) {
        return ReflectionUtils.newInstance(SavableExternalExtensions.REG.get(obj.getClass()), obj);
    }
}
