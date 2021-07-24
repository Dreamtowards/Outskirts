package general.vector;


class vec<S, T> {

    @private
    T[S] v;

    @private
    @operator.plus
    vec<S, T> op_add(vec<S, T> right) {
        auto v = new vec<S, T>();
        for (int i = 0;i < S;i++) {
            v.v[i] = this.v[i] + right.v[i];
        }
        return v;
    }

    @private
    @operator.minus
    vec<S, T> op_sub(vec<S, T> right) {
        return op_add(right.op_negate());
    }

    @private
    @operator.negate
    vec<S, T> op_negate() {
        return op_scale(-1);
    }

    @private
    @operator.mul
    vec<S, T> op_scale(T right) {
        auto v = new vec<S, T>();
        for (int i = 0;i < S;i++) {
            v.v[i] = this.v[i] * right;
        }
        return v;
    }

    @private
    @operator.div
    vec<S, T> op_div_s(T right) {
        return op_scale(1 / right);
    }

    @final
    vec<S, T> normalize() {
        return op_scale(1 / length());
    }

    @virtual
    T length_squared() {
        T s = 0;
        for (T f : v) {
            s += f*f;
        }
        return s;
    }

    @final
    T length() {
        return math.sqrt(length_squared());
    }


}


