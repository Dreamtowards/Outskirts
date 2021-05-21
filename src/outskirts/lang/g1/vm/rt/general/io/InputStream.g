package general.io;

// ByteArrayInputStream
// FileInputStream
//
class InputStream extends Closeable {

    // -1, 255
    public int read();

    public int read(byte[] buf, int off, int len);

    public final int read(byte[] buf) {
        return read(buf, 0, buf.length);
    }

    public int available();

    // public long skip(long n);

}