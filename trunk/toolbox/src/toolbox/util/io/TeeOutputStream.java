package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * TeeOutputStream is an output stream that splits the data and sends the
 * output to two delegate output streams.
 */
public class TeeOutputStream extends FilterOutputStream
{
    private OutputStream stream1_;
    private OutputStream stream2_;

    public TeeOutputStream(OutputStream stream1, OutputStream stream2)
    {
        super(stream1);
        stream1_ = stream1;
        stream2_ = stream2;
    }

    public synchronized void write(int b) throws IOException
    {
        stream1_.write(b);
        stream2_.write(b);
    }

    public synchronized void write(byte[] data, int offset, int length)
        throws IOException
    {
        stream1_.write(data, offset, length);
        stream2_.write(data, offset, length);
    }

    public void flush() throws IOException
    {
        stream1_.flush();
        stream2_.flush();
    }

    public void close() throws IOException
    {
        stream1_.close();
        stream2_.close();
    }
}
