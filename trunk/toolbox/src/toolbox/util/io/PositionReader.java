package toolbox.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Position based reader
 */
public class PositionReader extends Reader
{
    private Reader in_;
    private long offset_ = 0;
    private long markOffset_ = 0;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a position reader
     * 
     * @param  reader  Reader to chain
     */
    public PositionReader(Reader reader)
    {
        super(reader);
        in_ = reader;
    }

    //--------------------------------------------------------------------------
    // Overridden from java.io.Reader
    //--------------------------------------------------------------------------

    /**
     * Read a single character.
     *
     * @return     Single character read
     * @throws     IOException  If an I/O error occurs
     */
    public int read() throws IOException
    {
        int read = in_.read();
        if (read != -1)
            offset_++;

        return read;
    }

    /**
      * Read characters into a portion of an array.
      * 
      * @param      array   Storage for read characters
      * @param      off     Offset in array to store chars
      * @param      len     NUmber of chars to read
      * 
      * @return     Number of characters read
      * @throws     IOException  If an I/O error occurs
      */
    public int read(char[] array, int off, int len) throws IOException
    {
        int read = in_.read(array, off, len);

        // count characters read
        if (read > 0)
        {
            offset_ += read;
        }

        return read;
    }

    /**
     * Skip characters.
     *
     * @param   n   Number of characters to skip
     * @return  Number of characters skipped
     * @throws  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException
    {
        long skipped = in_.skip(n);

        offset_ += skipped;

        return skipped;
    }

    /**
     * Tell whether this stream is ready to be read.
     *
     * @return  True if read, false otherwise
     * @throws  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException
    {
        return in_.ready();
    }

    /**
     * Tell whether this stream supports the mark() operation.
     * 
     * @return  True if mark supported, false otherwise
     */
    public boolean markSupported()
    {
        return in_.markSupported();
    }

    /**
     * Mark the present position in the stream.
     *
     * @param      readAheadLimit  Read ahead limit
     * @throws     IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException
    {
        in_.mark(readAheadLimit);
        markOffset_ = offset_;
    }

    /**
     * Reset the stream.
     *
     * @throws  IOException  If an I/O error occurs
     */
    public void reset() throws IOException
    {
        in_.reset();
        offset_ = markOffset_;
    }

    /**
     * Close the stream.
     *
     * @throws  IOException  If an I/O error occurs
     */
    public void close() throws IOException
    {
        in_.close();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @param  stopAt  Character to stop at
     * @return Characters from the current postion until the stopAt or 
     *         EOF is found.
     * @throws IOException on IOError
     */
    public String readUntil(char stopAt) throws IOException
    {
        StringBuffer sb = new StringBuffer(256);

        synchronized (lock)
        {
            int c;

            while ((c = read()) != -1)
            {
                sb.append((char) c);

                if (c == stopAt)
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * @return  Offset
     */
    public long getOffset()
    {
        return offset_;
    }
}
