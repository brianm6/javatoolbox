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

    /**
     * Creates a TeeOutputStream
     * 
     * @param  stream1  First stream
     * @param  stream2  Second stream
     */
    public TeeOutputStream(OutputStream stream1, OutputStream stream2)
    {
        super(stream1);
        stream1_ = stream1;
        stream2_ = stream2;
    }

    /**
     * Writes to streams
     * 
     * @param  b  Byte to write
     * @throws IOException on IO error
     */
    public synchronized void write(int b) throws IOException
    {
        stream1_.write(b);
        stream2_.write(b);
    }

    /**
     * Writes to streams
     * 
     * @param  data    Data to write
     * @param  offset  Offset in data to start writing from
     * @param  length  Number of bytes to write
     * @throws IOException on IO error
     */
    public synchronized void write(byte[] data, int offset, int length)
        throws IOException
    {
        stream1_.write(data, offset, length);
        stream2_.write(data, offset, length);
    }

    /**
     * Flushes streams
     * 
     * @throws  IOException on IO error
     */
    public void flush() throws IOException
    {
        stream1_.flush();
        stream2_.flush();
    }

    /**
     * Closes streams
     * 
     * @throws IOException on IO error
     */
    public void close() throws IOException
    {
        stream1_.close();
        stream2_.close();
    }
}