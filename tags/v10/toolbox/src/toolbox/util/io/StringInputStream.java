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
    int size;

    /** current position in stream **/
    int index;

    /** stream buffer **/
    String buf;

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
     */
    public int read() throws IOException 
    {
        if (index < size)
            return buf.charAt(index++);
        else
            return -1;
    }
}