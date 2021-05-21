package general.util.vector;

class vector<N, T extends number> {

    array<T> _data = array<>(N);

    T lengthSquared() {
        T sum = 0;
        for (T e : _data) {
            sum += e;
        }
        return sum;
    }

    @final
    T length() {
        return Math.sqrt(lengthSquared());
    }

    @operator
    boolean ==(any right) {
        if (ptr(this) == ptr(right))
            return true;
        if (right is vector)
            return _data == right._data;
        return false;
    }

    operator .(any right) {

    }

}