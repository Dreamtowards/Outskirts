package outskirts.util;

public final class BytesConvert {

    public static byte[] toByteArray(int[] data) {
        byte[] out = new byte[data.length * 4];
        for (int i = 0;i < data.length;i++) {
            IOUtils.writeInt(out, i*4, data[i]);
        }
        return out;
    }

    public static byte[] toByteArray(float[] data) {
        byte[] out = new byte[data.length * 4];
        for (int i = 0;i < data.length;i++) {
            IOUtils.writeInt(out, i*4, Float.floatToIntBits(data[i]));
        }
        return out;
    }



    public static int[] toIntArray(byte[] bytes) {
        int[] out = new int[bytes.length / 4];
        for (int i = 0;i < out.length;i++) {
            out[i] = IOUtils.readInt(bytes, i*4);
        }
        return out;
    }

    public static float[] toFloatArray(byte[] bytes) {
        float[] out = new float[bytes.length / 4];
        for (int i = 0;i < out.length;i++) {
            out[i] = Float.intBitsToFloat(IOUtils.readInt(bytes, i*4));
        }
        return out;
    }
}
