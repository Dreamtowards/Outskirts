
namespace stl.lang;

class System {

    static void memcpy(byte* src, byte* dest, int size) {
        while(size-- != 0) {
            *dest = *src;
            dest++;
            src ++;
        }
    }

}