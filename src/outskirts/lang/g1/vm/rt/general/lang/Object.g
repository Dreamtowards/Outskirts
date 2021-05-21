package general.lang;

class Object {

    public Class<?> getClass();

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return System.hashCode(this);
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}