package toolbox.util.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * FixedWidthWriter
 */
public class FixedWidthWriter extends FilterWriter
{
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param  writer  Writer
     */
    public FixedWidthWriter(Writer writer)
    {
        super(writer);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    public void writeRPad(String str, int width) throws IOException
    {
        writeRPad(str, width, ' ');
    }

    public void writeRPad(String str, int width, char padChar)
        throws IOException
    {
        write(str, width, padChar, true);
    }

    public void writeLPad(String str, int width) throws IOException
    {
        writeLPad(str, width, ' ');
    }

    public void writeLPad(String str, int width, char padChar)
        throws IOException
    {
        write(str, width, padChar, false);
    }

    /**
     * Helps in the formatting of of FixedWidth Data.
     * Left/Right pad a string to a given width 
     * using a pad character. Strings longer than the 
     * width are truncated.
     *
     * @param   str      String to justify
     * @param   width    Width of resulting screen
     * @param   padChar  Character to use for padding
     * @param   padRight if true, pad on the right; if false, pad on the left
     */
    public void write(String str, int width, char padChar, boolean padRight)
        throws IOException
    {
        int strLen = (str == null ? 0 : str.length());

        if (strLen >= width)
        {
            write(str, 0, width);
        }
        else if (padRight)
        {
            if (strLen > 0)
                write(str);

            for (int i = strLen; i < width; ++i)
                write(padChar);
        }
        else
        {
            for (int i = strLen; i < width; ++i)
                write(padChar);

            if (strLen > 0)
                write(str);
        }
    }
}