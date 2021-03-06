package lisken.systoolbox;

import java.io.IOException;
import java.io.OutputStream;

public class BufferedFDOutputStream extends OutputStream {

    private long file;
    private int fd = -1;

    private native long init(int fd);

    private native long openFile(byte[] filename, boolean append);

    private native void nWrite(long file, int b);

    private native void nFlush(long file);

    private native void nClose(long file);

    /*
    static {
    System.loadLibrary("BufferedFDOutputStream");
    }
     */
    public BufferedFDOutputStream(int fildes)
            throws IOException {
        fd = fildes;
        file = init(fd);
    }

    public BufferedFDOutputStream(String filename)
            throws IOException {
        this(filename, false);
    }

    public BufferedFDOutputStream(String filename, boolean append)
            throws IOException {
        file = openFile(filename.getBytes(), append);
    }

    @Override
    public void write(int b)
            throws IOException {
        nWrite(file, b);
    }

    public void write(String s)
            throws IOException {
        write(s.getBytes());
    }

    @Override
    public void flush() {
        nFlush(file);
    }

    @Override
    public void close() {
        nClose(file);
        file = 0;
    }

    public int getFD() {
        return fd;
    }

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public String toString() {
        return "BufferedFDOutputStream[" + fd + "]";
    }
}

