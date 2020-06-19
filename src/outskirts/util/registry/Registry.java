package outskirts.util.registry;

import outskirts.util.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Registry<T> {

    protected HashMap<String, T> map = new HashMap<>();

    public abstract T register(T entry);

    protected final T register(String registryID, T entry) {
        Validate.notNull(entry, "Registration must not be null (%s)", registryID);
        Validate.notNull(registryID, "RegistryID must not be null (%s)", entry.getClass().toString());
        Validate.validState(!containsKey(registryID), "%s's registryID '%s' is already been registered.", entry.toString(), registryID);

        map.put(registryID, entry);

        return entry;
    }

    public int size() {
        return map.size();
    }

    public Collection<T> values() {
        return map.values();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public T get(String registryID) {
        return map.get(registryID);
    }

    public boolean containsKey(String registryID) {
        return map.containsKey(registryID);
    }

    public static class ClassRegistry<T extends Class<? extends Registrable>> extends Registry<T> {
        @Override
        public T register(T entry) {
            try {
                return register(entry.newInstance().getRegistryID(), entry);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("Failed to create new instance, require a Empty&Accessible constructor", ex);
            }
        }
    }

    public static class RegistrableRegistry<T extends Registrable> extends Registry<T> {
        @Override
        public T register(T entry) {
            return register(entry.getRegistryID(), entry);
        }
    }

}
