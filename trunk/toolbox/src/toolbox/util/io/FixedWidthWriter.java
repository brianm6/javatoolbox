package toolbox.util.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * FixedWidthWriter enables writing of strings of a fixed lengh with padding on
 * either the left of the right.
 */
public class FixedWidthWriter extends FilterWriter
{
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FixedWidthWriter
     * 
     * @param  writer  Writer to chain
     */
    public FixedWidthWriter(Writer writer)
    {
        super(writer);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Writes right padded
     * 
     * @param   str    String to write
     * @param   width  Max width
     * @throws  IOException on I/O error
     */
    public void writeRPad(String str, int width) throws IOException
    {
        writeRPad(str, width, ' ');
    }


    /**
     * Writes right padded
     * 
     * @param  str      String to write
     * @param  width    Max width
     * @param  padChar  Pad character
     * @throws IOException on I/O error
     */
    public void writeRPad(String str, int width, char padChar)
        throws IOException
    {
        write(str, width, padChar, true);
    }


    /**
     * Writes left padded
     * 
     * @param  str    String to write
     * @param  width  Max width
     * @throws IOException on I/O error
     */
    public void writeLPad(String str, int width) throws IOException
    {
        writeLPad(str, width, ' ');
    }


    /**
     * Writes left padded
     * 
     * @param  str      String to write
     * @param  width    Max width
     * @param  padChar  Pad character
     * @throws IOException on I/O error
     */
    public void writeLPad(String str, int width, char padChar)
        throws IOException
    {
        write(str, width, padChar, false);
    }

    
    /**
     * Helps in the formatting of of FixedWidth Data. Left/Right pad a string 
     * to a given width using a pad character. Strings longer than the width 
     * are truncated.
     *
     * @param   str       String to justify
     * @param   width     Width of resulting screen
     * @param   padChar   Character to use for padding
     * @param   padRight  If true, pad on the right; if false, pad on the left
     * @throws  IOException on I/O error
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