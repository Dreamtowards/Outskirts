package outskirts.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    public static long write(InputStream inputStream, OutputStream outputStream) throws IOException {
        return write(inputStream, outputStream, DEFAULT_BUFFER);
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(inputStream, out);
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

    public static void writeShort(byte[] b, int off, short s) {
        b[off]   = (byte)(s >> 16);
        b[off+1] = (byte)(s);
    }
    public static void writeInt(byte[] b, int off, int i) {
        b[off]   = (byte)(i >> 24);
        b[off+1] = (byte)(i >> 16);
        b[off+2] = (byte)(i >> 8);
        b[off+3] = (byte)(i);
    }
    public static void writeLong(byte[] b, int off, long l) {
        b[off]   = (byte)(l >> 56);
        b[off+1] = (byte)(l >> 48);
        b[off+2] = (byte)(l >> 40);
        b[off+3] = (byte)(l >> 32);
        b[off+4] = (byte)(l >> 24);
        b[off+5] = (byte)(l >> 16);
        b[off+6] = (byte)(l >> 8);
        b[off+7] = (byte)(l);
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


}
