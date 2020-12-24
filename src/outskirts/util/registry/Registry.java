package outskirts.util.registry;

import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.*;

public final class Registry<T extends Registrable> {

    private List<T> entries = new ArrayList<>();
    private List<String> syncedkeys = new ArrayList<>();   // synced with entries

    public T register(T entry) {
        String registryID;
        Validate.notNull(entry, "Registration Entry must be nonnull. (%s)", entry);
        Validate.notNull(registryID=entry.getRegistryID(), "RegistryID must be nonnull. (%s)", entry);
        Validate.validState(!containsKey(registryID), "RegistryID \"%s\" already been registered.", registryID);

        entries.add(entry);

        rebuildSyncedKeys();

        return entry;
    }

    public int size() {
        return entries.size();
    }

    public List<T> values() {
        return Collections.unmodifiableList(entries);
    }

    public List<String> keys() {
        return Collections.unmodifiableList(syncedkeys);
    }

    public T get(String registryID) {
        return entries.get(indexOf(registryID));
    }

    public boolean containsKey(String registryID) {
        return indexOf(registryID) != -1;
    }

    public int indexOf(String registryID) {
        return syncedkeys.indexOf(registryID);
    }


    private void rebuildSyncedKeys() {
        syncedkeys.clear();
        for (T e : entries) {
            syncedkeys.add(e.getRegistryID());
        }
    }
}
