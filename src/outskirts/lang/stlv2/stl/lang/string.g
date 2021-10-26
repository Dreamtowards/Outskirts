
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

    bool is_empty() {
        return this->length() == 0;
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







// int length();  bool is_empty();
// byte char_at(int i);

// <equals>;
// bool equals_ignore_case();
// byte find(byte ch, int from);  int find(string s, int from);
// int find_reverse(string ch, int from);
//   bool starts_with(string prefix, int from);
//   bool ends_with(string suffix);
//   bool contains(string s);

// int hashcode();
// string substring(int begin, int end);
// string replaceAll/First/<Last>(..);
// string[] split(string delimiter);
// string to_lower_case();  string to_upper_case();
// string trim();  string trim_leading();  string trim_trailing();  string trim_indent();
// bool is_blank();

// string[] lines();
// string repeat(int n);
// static string join(string delimiter, string[] elements);
// string ?indent(int n);  escapes  unescape  format

}