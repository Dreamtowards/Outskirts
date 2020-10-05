package outskirts.util;

import java.util.*;

// no recormended.
public final class ObjectPool<T> {

    private static final ThreadLocal<Map<Class<?>, ObjectPool<?>>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    @SuppressWarnings("unchecked")
    public static <T> ObjectPool<T> get(Class<T> clazz) {
        return (ObjectPool<T>) THREAD_LOCAL.get().computeIfAbsent(clazz, k -> new ObjectPool<>(clazz));
    }

    private Class<T> type;

    private LinkedList<T> list = new LinkedList<>();

    private ObjectPool(Class<T> clazz) {
        this.type = clazz;
    }

    public T get() {
        if (list.isEmpty()) {
            return newInstance();
        } else {
            return list.removeLast();
        }
    }

    public void release(T obj) {
        list.add(obj);
    }

    private T newInstance() {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create ObjectPool elements instance.", ex);
        }
    }

}
