package toolbox.util.io;

import java.io.OutputStream;

/**
 * An output stream that flows into a StringBuffer
 */
public class StringOutputStream extends OutputStream
{
    /** output buffer **/
    StringBuffer output;

    /**
     * Create a new StringOutputStream with the default initial buffer size.
     */
    public StringOutputStream()
    {
        output = new StringBuffer();
    }

    /**
     * Create a new StringOutputStream with the specified initial buffer size.
     *
     * @param initialBuffereSize  The size of the initial buffer
     */
    public StringOutputStream(int initialBufferSize)
    {
        output = new StringBuffer(initialBufferSize);
    }

    /**
     * Close the stream - no-op
     */
    public void close()
    {
        // nothing to close
    }

    /**
     * Flush the stream - no-op
     */
    public void flush()
    {
        // nothing to flush
    }

    /**
     * @return The stringbuffer
     */
    public StringBuffer getBuffer()
    {
        return output;
    }

    /**
     * @return the output as a String.
     */
    public String toString()
    {
        return output.toString();
    }

    /**
     * Write a single character. Ignores end of file.
     *
     * @param  outputChar  The character to write to the stream
     */
    public void write(int outputChar)
    {
        if (outputChar != -1)
            output.append((char) outputChar);
    }
}