package toolbox.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * StringInputStream. Enables a string to be streamed as
 * a source of input.
 */
public class StringInputStream extends InputStream 
{

    /** size of string **/
    private int size;

    /** current position in stream **/
    private int index;

    /** stream buffer **/
    private String buf;

    /**
     * Creates a StringInputStream with the passed string
     *
     * @param  s  Sting to stream
     */
    public StringInputStream(String s)
    {
        size = s.length();
        index = 0;
        buf = s;
    }

    /**
     * Reads a byte from the stream
     *
     * @return The current character or -1 if stream is empty
     * @throws IOException if IO error occurs
     */
    public int read() throws IOException 
    {
        if (index < size)
            return buf.charAt(index++);
        else
            return -1;
    }
    
    /**
     * Returns number of bytes available to read from the stream
     * 
     * @return  Number of bytes available
     * @throws  IOException
     */
    public int available() throws IOException
    {
        return size - index;
    }
}