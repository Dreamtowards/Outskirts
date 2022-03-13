
namespace stl.lang;

using static stl.lang.System.memcpy;

class String {

    byte* base;

    byte charAt(int i) {
        return *(byte*)((int)this->base + i);
    }

    int length() {
        int i = 0;
        while ((bool)this->charAt(i)) {
            i++;
        }
        return i;
    }

    bool isEmpty() {
        return this->length() == 0;
    }

    String substring(int begin, int end) {
        int len = end-begin;
        byte* p = new(len + 1);
        memcpy( (byte*)((int)this->base + begin), p, len);
        *(byte*)((int)p + len) = (byte)0;

        String s;
        s.base = p;
        return s;
    }
    String substring(int begin) {
        return this->substring(begin, this->length());
    }

    String repeat(int n) {
        int l = this->length();
        int len = l * n;
        String s;
        s.base = new(len + 1);

        int i = 0;
        while (i < n) {
            memcpy(this->base, (byte*)((int)s.base + i*n), l);
            i++;
        }
        *(byte*)((int)s.base + len) = (byte)0;
        return s;
    }


    bool startsWith(String s, int from) {
        int i = 0;
        int len = s.length();
        if (this->length() - from < len) return false;

        while (i < len) {
            if ((int)this->charAt(from+i) != (int)s.charAt(i))
                return false;
            i++;
        }
        return true;
    }

    bool endsWith(String s) {
        return this->startsWith(s, this->length()-s.length());
    }


    int find(String s, int from) {
        int i = from;
        int end = this->length()-s.length();
        while (i <= end) {
            if (this->startsWith(s, i))
                return i;
            i++;
        }
        return 99;
    }
    int find(String s) {
        // return 2;
        return this->find(s, 0);
    }






/*
    bool contains(string s) {
        return this->find(s) != 99;
    }

    bool _equals(string s) {
        // use of &&
        if (s.length() == this->length()) {
            if (this->find(s) == 0) {
                return true;
            }
        }
        return false;
    }

    int rfind(string s, int from) {
        // if (from > this->length() - s.length()) EXCEPTION;
        int i = from;
        while (i >= 0) {
            if (this->starts_with(s, i))
                return i;
            i--;
        }
        return 99;
    }

    int rfinds(string s) {
        return this->find_reverse_on(s, this->length() - s.length());
    }

    int findUnblank() {
        int i = 0;
        int len = this->length();
        while (i < len) {
            if ((int)this->char_at(i) > ' ')
                return i;
            i++;
        }
        return 99;
    }
    int rfindUnblank() {
        int i = this->length()-1;
        while (i >= 0) {
            if ((int)this->char_at(i) > ' ')
                return i;
            i--;
        }
        return 99;
    }
    bool isBlank() {
        return this->findUnblank() == 99;
    }

    string trim() {
        int begin = this->find_unblank();
        int end =   this->rfind_unblank()+1;  // +1: includes the unblank char.
        return this->substring(begin, end);
    }
    string trim_leading() {
        int begin = this->find_unblank();
        return this->substr(begin);
    }
    string trim_trailing() {
        int end =   this->rfind_unblank()+1;
        return this->substring(0, end);
    }
*/






// <equals>;
// bool equals_ignore_case();

// int hashcode();
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