
namespace stl.lang;

using static stl.lang.memory.memcpy;

class string {

    byte* base;

    int hash;

    byte char_at(int i) {
        return *(byte*)((int)this->base + i);
    }

    int length() {
        int i = 0;
        while ((int)(*this).char_at(i) != 0) {
            i++;
        }
        return i;
    }


    string substring(int begin, int end) {
        int len = end - begin;
        byte* p = new(len + 1);
        memcpy( (byte*)((int)this->base + begin), p, len);
        *(int*)((int)p + len) = 0;

        string s = string();
        s.base = p;
        s.hash = 31;
        return s;
    }
/*
    int find(string s) {
        int i = 0;
        int len = this->length();
        int dstlen = s.length();
        while (i < len) {
            int j = 0;
            bool found = true;
            while (j < dstlen) {
                if (this->char_at(i) != s.char_at(j)) {
                    found = false;
                    break;
                }
                if (found)
                    return i;
                j++;
            }
            i++;
        }
        return -1;
    }
*/
}