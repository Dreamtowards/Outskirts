
namespace stl.lang;

class string {

    int* base;

    //@private int hash;
    //@private array<ushort> value;

    //string init(array<ushort> value) {
    //    this.value = arrays.copy(value);
    //}

    int char_at(int i) {
        return *(int*)((int)(*this).base + sizeof(int) * i);
    }

    int length() {
        int i = 0;
        while ((*this).char_at(i) != 0) {
            i = i + 1;
        }
        return i;
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
    }*/
}