package outskirts.util;

/**
 * non finish yet
 * tail data in long
 */
public class BitArray {

    private long[] storage;
    private int bits;
    private long maxValue;
    public final int length;

    /**
     * @param bits Bits Pre Array-Element
     */
    private BitArray(int bits, int length, long[] storage) {
        Validate.isTrue(bits <= 32, "bits must less than 32"); //because a long64 can't storage 2x 48bit element
        this.length = length;
        this.bits = bits;
        this.maxValue = (1L << bits) - 1L;
        this.storage = storage;
    }

    public BitArray(int bits, int length) {
        this(bits, length, new long[Maths.ceil((bits * length) / 64f)]);
    }

    public BitArray(int bits, int length, byte[] storageBytes) {
        this(bits, length, BitArray.resolveBytes(storageBytes));
    }

    public void set(int index, long value) {
        Validate.isTrue(index >= 0 && index < length, "IndexOutOfBound. (index=%s, length=%s)", index, length);
        Validate.isTrue(value <= maxValue, "Value out bits range. (max=%s, actual=%s)", maxValue, value);

        int bitIndex = index * bits;
        int arrayPos = bitIndex / 64;
        int offsetBits = bitIndex % 64;

        long data = ((long)value & maxValue) << (64 - offsetBits - bits);

        storage[arrayPos] = storage[arrayPos] | data;
    }

    public int get(int index) {
        Validate.isTrue(index >= 0 && index < length, "IndexOutOfBound. (index=%s, length=%s)", index, length);

        int bitIndex = index * bits;
        int arrayPos = bitIndex / 64;
        int offsetBits = bitIndex % 64;

        return (int) ((storage[arrayPos] >> (64 - offsetBits - bits)) & maxValue);
    }

    public byte[] exportBytes() {
        byte[] bytes = new byte[storage.length * 8];
        int j = 0;
        for (int i = 0;i < storage.length;i++) {
            bytes[j++] = (byte)((storage[i] >> 56) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 48) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 40) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 32) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 24) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 16) & 0xFF);
            bytes[j++] = (byte)((storage[i] >> 8) & 0xFF);
            bytes[j++] = (byte)(storage[i] & 0xFF);
        }
        return bytes;
    }

    private static long[] resolveBytes(byte[] bytes) {
        long[] storage = new long[bytes.length / 8];
        int j = 0;
        for (int i = 0;i < storage.length;i++) {
            storage[i] =    ((long) bytes[j++] << 56) |
                            ((long) bytes[j++] << 48) |
                            ((long) bytes[j++] << 40) |
                            ((long) bytes[j++] << 32) |
                            ((long) bytes[j++] << 24) |
                            ((long) bytes[j++] << 16) |
                            ((long) bytes[j++] << 8) |
                            bytes[j++];
        }
        return storage;
    }
}
