

using i8 as byte;
using i32 as int;

namespace stl::lang;

class System {

    static void memcpy(byte* src, byte* dest, int size) {
        while(size-- != 0) {
            *dest = *src;
            dest++;
            src ++;
        }
    }

}