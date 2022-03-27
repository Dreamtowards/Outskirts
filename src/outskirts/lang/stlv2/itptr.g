
using stl::lang::System;
using stl::lang::String;

namespace test;

class vec2<T> {
    T x;
    T y;

    T sum() {
        return this->x;
    }
}

class _Main {

    static int running;

    bool tes1;

    static void main() {

       // getstatic Classname::sfieldname
       // getaddr STATIC_BASE+idxOffset("Classname::sfieldname")
        int i2 = 0;
        String::asv = 6;
        int b = String::asv;

        /* String s1;
        s1.base = " kabcTex234";


        String s2;
        s2.base = "Tex";
        int id = s1.find(s2);


        /*
        String v = s2.repeat(3);
        int len = v.length();

/*
        vec2<int> v;
        v.x = 8;
        v.y = 9;
        int vfv = v.sum();

        vec2<byte> v2;
        v2.x = (byte)1;
        v2.y = (byte)2;

        byte b2 = v2.x;

        int sz1 = sizeof(vec2<int>);
        int sz2 = sizeof(vec2<byte>);
        int sz3 = sizeof(vec2i);
*/

/*
        v.x = 8;
        v.y = 9;

        int vl = v.x;

        int sz = sizeof(vec2<byte>); // 2 +4
        int sz2 = sizeof(vec2<int>);  // 8 +4

        string s = string();
        s.hash = 21;
        s.base = "okStr";*/

    }
}
