package general.lang;


class OutputStream extends Closeable {

    public void write(int b);

    public void write(byte[] buf, int off, int len);

    public final void write(byte[] buf) {
        write(buf, 0, buf.length);
    }

    public void flush();
}