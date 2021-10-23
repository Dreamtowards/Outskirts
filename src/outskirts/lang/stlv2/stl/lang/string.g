
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
        while ((bool)this->char_at(i)) {
            i++;
        }
        return i;
    }

    int nest() {
        return this->length();
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

    int gp() { return (int)this; }

    int find(string s) {
        int len = this->length();
        int dstlen = s.length();
        int i = 0;
        while (i <= len-dstlen) {
            int j = 0;
            int found = 1;
            while (j < dstlen) {
                //return (int)this->char_at(i+j);
                if ((int)this->char_at(i+j) != (int)s.char_at(j)) {
                    found = 0;
                    break;
                }
                j++;
            }
            if (found != 0)
                return i;
            i++;
        }
        return 89;
    }

}