package toolbox.util.io;

import java.io.Writer;

/**
 * Writer analogous to /dev/null
 */
public class NullWriter extends Writer
{
    /**
     * Closes the stream
     */
    public void close()
    {
        // Nothing to do
    }

    /**
     * Flushes the stream
     */
    public void flush()
    {
        // Nothing to do
    }

    /**
     * Eat written characters
     */
    public void write(char[] cbuf, int off, int len)
    {
    }
}