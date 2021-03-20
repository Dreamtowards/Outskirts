package outskirts.util.registry;

import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.*;

public final class Registry<T extends Registrable> {

    private List<T> entries = new ArrayList<>();
    private List<String> syncedkeys = new ArrayList<>();   // synced with entries

    public T register(T entry) {
        if (entry == null) {  // allowed 'null' holdplacer exist in zero-idx.
            assert size()==0;
        } else {
            String registryID = Objects.requireNonNull(entry.getRegistryID());
            assert !containsKey(registryID) : "RegistryID '"+registryID+"' has already been registered.";
        }

        entries.add(entry);
        rebuildSyncKeys();

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
        int i = indexOf(registryID);
        if (i==-1) return null;
        return entries.get(i);
    }

    public boolean containsKey(String registryID) {
        return indexOf(registryID) != -1;
    }

    public int indexOf(String registryID) {
        return syncedkeys.indexOf(registryID);
    }


    private void rebuildSyncKeys() {
        syncedkeys.clear();
        for (T e : entries) {
            syncedkeys.add(e == null ? null : e.getRegistryID());
        }
    }
}
