package general.lang;


class Map<K, V> {

    public abstract int size();

    public abstract boolean containsKey(K k);

    public abstract boolean containsValue(V v);

    public abstract V get(K k);

    public abstract V put(K k, V v);

    public abstract V remove(K k);

    public abstract void clear();


    public abstract Set<K> keySet();

    public abstract Collection<V> values();  //

    public abstract Set<Entry<K, V>> entrySet();


    public abstract boolean equals(Object o);

    public abstract int hashCode();


    public boolean isEmpty() {
        return size() == 0;
    }

    public abstract void putAll(Map<K, V> m) {
        for (Map.Entry<K, V> e : m.entries()) {
            put(e.key, e.value);
        }
    }



}