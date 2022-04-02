package general.lang;

import general.util.arrays;

@final
class string : hashable {

    @private
    int hash;

    @private
    array<ushort> value;

    @constructor
    void init() {

    }

    string substring(int begin_idx, int end_idx) {
        return new string(arrays.subarray(value, begin_idx, end_idx));
    }

    ushort char_at(int i) {
        return value.get(i);
    }

    @private
    @operator.equals
    boolean op_equals(string r) {
        if (r.length != length)
            return false;
        if (r.hashcode() != hashcode())
            return false;

        int len = array.length;
        for (int i = 0;i < len;i++) {
            if (r.char_at(i) != char_at(i))
                return false;
        }
        return true;
    }

    @override
    int hashcode() {
        if (!hash && value.length) {
            hash = arrays.hashcode(value);  // if (!hash) hash = -1;
        }
        return hash;
    }

}

