package toolbox.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that supports positioning
 */
public class PositionInputStream extends InputStream
{
    private InputStream in_;
    private long offset_ = 0;
    private long markOffset_ = 0;
    private int lastByteRead_ = -1;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for PositionInputStream
     * 
     * @param  iStream  Input stream
     */
    public PositionInputStream(InputStream iStream)
    {
        if (iStream == null)
            throw new NullPointerException();

        in_ = iStream;
    }

    //--------------------------------------------------------------------------
    // Overridden from java.io.InputStream
    //--------------------------------------------------------------------------

    /**
     * Read a single byte.
     *
     * @return     Single byte that was read
     * @throws     IOException  If an I/O error occurs
     */
    public int read() throws IOException
    {
        int read = in_.read();
        if (read != -1)
            offset_++;

        lastByteRead_ = read;
        return read;
    }

    /**
     * Read a byte into a portion of an array.
     * 
     * @param   b       Byte array to store read data
     * @param   off     Offset in byte array to store read data
     * @param   len     Number of bytes to copy to byte array
     * @return  Number of bytes read
     * @throws  IOException  If an I/O error occurs
     */
    public int read(byte b[], int off, int len) throws IOException
    {
        int read = in_.read(b, off, len);

        // count characters read
        if (read > 0)
        {
            offset_ += read;
            lastByteRead_ = b[off + read - 1];
        }

        return read;
    }

    /**
     * Skip characters.
     *
     * @param    n  Number of bytes to skip
     * @return   Number of characters skipped
     * @throws   IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException
    {
        long skipped = in_.skip(n);

        offset_ += skipped;

        return skipped;
    }

    /**
     * Returns number of bytes available to read from stream without blocking
     * 
     * @return  Number of bytes available to read
     * @throws  IOException on IO error
     */
    public int available() throws IOException
    {
        return in_.available();
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException
    {
        in_.close();
    }

    /**
     * Tell whether this stream supports the mark() operation.
     * 
     * @return  True if mark is supported, false otherwise
     */
    public boolean markSupported()
    {
        return in_.markSupported();
    }

    /**
     * Mark the present position in the stream.
     *
     * @param readAheadLimit  Read ahead limit
     */
    public void mark(int readAheadLimit)
    {
        in_.mark(readAheadLimit);
        markOffset_ = offset_;
    }

    /**
     * Reset the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void reset() throws IOException
    {
        in_.reset();
        offset_ = markOffset_;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Reads until a given byte
     * 
     * @param   stopAt  Byte to stop at
     * @return  Bytes from the current postion until the stopAt or EOF is found.
     * @throws  IOException on IO error
     */
    public byte[] readUntil(byte stopAt) throws IOException
    {
        return readUntil(new byte[] { stopAt });
    }

    /**
     * Reads until any number of given bytes
     * 
     * @param   stopAt  Any byte to stop at
     * @return  Bytes from the current postion until the stopAt or EOF is found.
     * @throws  IOException on IO error
     */
    public byte[] readUntil(byte[] stopAt) throws IOException
    {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(256);

        synchronized (this)
        {
            int b;

            outer : while ((b = read()) != -1)
            {
                oStream.write(b);
                for (int i = 0; i < stopAt.length; i++)
                {
                    if (b == stopAt[i])
                        break outer;
                }
            }

        }

        return oStream.toByteArray();
    }

    /**
     * @return  Current offset read using the InputStream
     */
    public long getOffset()
    {
        return offset_;
    }

    /**
     * @return Last byte read by this InputStream.
     */
    public int getLastByteRead()
    {
        return lastByteRead_;
    }
}
