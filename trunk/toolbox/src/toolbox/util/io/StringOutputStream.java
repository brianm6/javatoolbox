package toolbox.util.io;

import java.io.OutputStream;

/**
 * An output stream that flows into a StringBuffer
 */
public class StringOutputStream extends OutputStream
{
    /** 
     * Output buffer 
     */
    private StringBuffer output_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a StringOutputStream
     */
    public StringOutputStream()
    {
        output_ = new StringBuffer();
    }

    //--------------------------------------------------------------------------
    //  Overrides java.io.OutputStream
    //--------------------------------------------------------------------------

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
     * Write a single character
     *
     * @param outputChar Character to write to the stream
     */
    public void write(int outputChar)
    {
        if (outputChar != -1)
            output_.append((char) outputChar);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns the contents of the the stream
     * 
     * @return String
     */
    public String toString()
    {
        return output_.toString();
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
        
    /**
     * Returns the StringBuffer backing the stream
     * 
     * @return StringBuffer
     */
    public StringBuffer getBuffer()
    {
        return output_;
    }
}