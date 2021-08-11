
package stl.lang;


class string {

    @private
    int hash;

    @private
    array<ushort> value;

    string init(array<ushort> value) {
        this.value = arrays.copy(value);
    }

    ushort char_at(int idx) {
        return sac_get(value, idx);
    }

/*
    string substring(int begin, int end) {
        s_print("DoSubStr");
        return new string(arrays.subarray(begin, end));
    }

    @override
    int hashcode() {
        if (hash == 0 && value.length) {
           hash = arrays.hash(value);  // won't return 0 since arrlen > 0.
        }
        return hash;
    }

*/
}