package toolbox.util.io;

import java.io.Writer;

/**
 * NullWriter sends all characters written to it to /dev/null.
 */
public class NullWriter extends Writer
{
    //--------------------------------------------------------------------------
    // Overrides java.io.Writer
    //--------------------------------------------------------------------------
    
    /**
     * Closes the stream.
     */
    public void close()
    {
        // Nothing to do
    }

    
    /**
     * Flushes the stream.
     */
    public void flush()
    {
        // Nothing to do
    }

    
    /**
     * Eat written characters.
     * 
     * @param cbuf Character buffer to write
     * @param off Offset into character buffer
     * @param len Number of characters to write
     */
    public void write(char[] cbuf, int off, int len)
    {
        // Do nothing
    }
}