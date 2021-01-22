package outskirts.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class IOUtils {

    private static final byte[] DEFAULT_BUFFER = new byte[2048];

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                closeQuietly(closeable);
            }
        }
    }

    public static long write(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
        long written = 0;
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
            written += length;
        }
        return written;
    }

    public static long write(InputStream is, OutputStream os) throws IOException {
        return write(is, os, DEFAULT_BUFFER);
    }
    public static long write(byte[] in, OutputStream os) throws IOException {
        return IOUtils.write(new ByteArrayInputStream(in), os);
    }
    public static long write(byte[] in, File file) throws IOException {
        return IOUtils.write(new ByteArrayInputStream(in), new FileOutputStream(file));
    }
    public static long write(String s, OutputStream outputStream) throws IOException {
        return IOUtils.write(s.getBytes(StandardCharsets.UTF_8), outputStream);
    }
    public static long write(String s, File file) throws IOException {
        return IOUtils.write(s.getBytes(StandardCharsets.UTF_8), new FileOutputStream(file));
    }



    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(is, out);
        return out.toByteArray();
    }

//    public static InputStream toInputStream(byte[] bytes, int off, int len) {
//        return new ByteArrayInputStream(bytes, off, len);
//    }
//    public static InputStream toInputStream(byte[] bytes) {
//        return new ByteArrayInputStream(bytes, 0, bytes.length);
//    }

    public static FloatBuffer fillBuffer(FloatBuffer buffer, float... data) {
        for (int i = 0;i < data.length;i++) {
            buffer.put(i, data[i]);
        }
        return buffer;
    }

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        return new String(toByteArray(inputStream), charset);
    }

    public static String toString(InputStream inputStream) throws IOException {
        return toString(inputStream, Charset.defaultCharset());
    }

    public static String toString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static short readShort(byte[] b, int off) { // ptr+=2bytes
        return (short) (((b[off] & 0xFF) << 16) | (b[off+1] & 0xFF));
    }
    public static int readInt(byte[] b, int off) { // ptr+=4bytes
        return  ((b[off]   & 0xFF) << 24) |
                ((b[off+1] & 0xFF) << 16) |
                ((b[off+2] & 0xFF) << 8 ) |
                 (b[off+3] & 0xFF);
    }
    public static long readLong(byte[] b, int off) { // ptr+=8bytes
        return  ((b[off]   & 0xFFL) << 56) |
                ((b[off+1] & 0xFFL) << 48) |
                ((b[off+2] & 0xFFL) << 40) |
                ((b[off+3] & 0xFFL) << 32) |
                ((b[off+4] & 0xFF) << 24) |
                ((b[off+5] & 0xFF) << 16) |
                ((b[off+6] & 0xFF) << 8 ) |
                 (b[off+7] & 0xFF);
    }

    public static byte[] writeShort(byte[] b, int off, short s) {
        b[off]   = (byte)(s >> 16);
        b[off+1] = (byte)(s);
        return b;
    }
    public static byte[] writeInt(byte[] b, int off, int i) {
        b[off]   = (byte)(i >> 24);
        b[off+1] = (byte)(i >> 16);
        b[off+2] = (byte)(i >> 8);
        b[off+3] = (byte)(i);
        return b;
    }
    public static byte[] writeLong(byte[] b, int off, long l) {
        b[off]   = (byte)(l >> 56);
        b[off+1] = (byte)(l >> 48);
        b[off+2] = (byte)(l >> 40);
        b[off+3] = (byte)(l >> 32);
        b[off+4] = (byte)(l >> 24);
        b[off+5] = (byte)(l >> 16);
        b[off+6] = (byte)(l >> 8);
        b[off+7] = (byte)(l);
        return b;
    }


    public static byte[] readFully(InputStream is, byte[] dest, int off, int len) throws IOException {
        if (is.read(dest, off, len) != len-off)
            throw new EOFException();
        return dest;
    }
    public static byte[] readFully(InputStream is, byte[] arr) throws IOException {
        return readFully(is, arr, 0, arr.length);
    }

    public static byte readByte(InputStream is) throws IOException {
        int d = is.read();
        if (d == -1)
            throw new EOFException();
        return (byte)d;
    }
    public static short readShort(InputStream is) throws IOException {
        return readShort(readFully(is, DEFAULT_BUFFER, 0, 2), 0);
    }
    public static int readInt(InputStream is) throws IOException {
        return readInt(readFully(is, DEFAULT_BUFFER, 0, 4), 0); // local var just for short call
    }
    public static long readLong(InputStream is) throws IOException {
        return readLong(readFully(is, DEFAULT_BUFFER, 0, 8), 0);
    }

    public static void writeFully(OutputStream os, byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
    }
    public static void writeFully(OutputStream os, byte[] b) throws IOException {
        writeFully(os, b, 0, b.length);
    }
    public static void writeByte(OutputStream os, byte b) throws IOException {
        os.write(b);
    }
    public static void writeShort(OutputStream os, short s) throws IOException {
        writeFully(os, writeShort(DEFAULT_BUFFER, 0, s), 0, 2);
    }
    public static void writeInt(OutputStream os, int i) throws IOException {
        writeFully(os, writeInt(DEFAULT_BUFFER, 0, i), 0, 4);
    }
    public static void writeLong(OutputStream os, long l) throws IOException {
        writeFully(os, writeLong(DEFAULT_BUFFER, 0, l), 0, 8);
    }


    public static float readFloat(InputStream is) throws IOException {
        return Float.intBitsToFloat(readInt(is));
    }
    public static void writeFloat(OutputStream os, float f) throws IOException {
        writeInt(os, Float.floatToIntBits(f));
    }
    /**
     * ISO 10646 .UTF-8 Specification.
     * but actually only supports 1bytes, 2bytes, and 3bytes char. (Unicode range U+0000 - U+FFFF.)
     * and in addition, write 2bytes (a unsigned-short) in start for utflen.
     */
    public static String readUTF(InputStream is) throws IOException {
        int utflen = IOUtils.readShort(is) & 0xFFFF;
        byte[] bytearr = new byte[utflen];
        char[] chararr = new char[utflen];
        readFully(is, bytearr);

        int bi = 0, ci = 0;
        int b, b2, b3;
        while (bi < utflen) {  // fastmode read ASCII.
            b = bytearr[bi] & 0xFF;
            if (b > 127) break;
            bi++;
            chararr[ci++] = (char)b;
        }
        while (bi < utflen) {
            b = bytearr[bi] & 0xFF;
            switch (b >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: // 0xxxxxxx
                    bi++;
                    chararr[ci++] = (char) b;
                    break;
                case 12: case 13: // 110xxxxx 10xxxxxx
                    b2 = bytearr[bi + 1]; bi+=2;
                    if ((b2 & 0xC0) != 0x80) throw new UTFDataFormatException();
                    chararr[ci++] = (char) (((b & 0x1F) << 6) | (b2 & 0x3F));
                    break;
                case 14: // 1110xxxx 10xxxxxx 10xxxxxx
                    b2 = bytearr[bi + 1]; b3 = bytearr[bi + 2]; bi+=3;
                    if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) throw new UTFDataFormatException();
                    chararr[ci++] = (char) (((b & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
                    break;
                default:
                    throw new UTFDataFormatException();
            }
        }
        return new String(chararr, 0, ci);
    }

    /**
     * UTF-8.
     * but in addition, write 2bytes (a unsigned-short) in start as utflen.
     */
    public static void writeUTF(OutputStream os, String s) throws IOException {
        int strlen = s.length(); int c; int bi=0;
        byte[] bytearr = DEFAULT_BUFFER;
        for (int i = 0;i < strlen;i++) {
            c = s.charAt(i);
            if (c <= 0x007F) {
                bytearr[bi++] = (byte)c;
            } else if (c <= 0x07FF) {
                bytearr[bi++] = (byte)(0xC0 | ((c >> 6) & 0x1F));
                bytearr[bi++] = (byte)(0x80 |  (c       & 0x3F));
            } else {
                bytearr[bi++] = (byte)(0xE0 | ((c >> 12) & 0x0F));
                bytearr[bi++] = (byte)(0x80 | ((c >>  6) & 0x3F));
                bytearr[bi++] = (byte)(0x80 |  (c        & 0x3F));
            }
        }
        if (bi > 0xFFFF) throw new IndexOutOfBoundsException();
        os.write(bi >> 8);
        os.write(bi & 0xFF);
        writeFully(os, bytearr, 0, bi);
    }

}
