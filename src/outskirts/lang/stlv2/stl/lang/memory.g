
namespace stl.lang;

class memory {

    static void memcpy(byte* src, byte* dest, int size) {
        while(size-- != 0) {
            *dest = *src;
            dest++;
            src ++;
        }
    }

}