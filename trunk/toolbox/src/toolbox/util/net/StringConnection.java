package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * String based implementation of an IConnection
 */
public class StringConnection implements IConnection
{
    /** Input stream **/
    StringInputStream sis;

    /** Output stream **/
    StringOutputStream sos;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for StringConnection
     */
    public StringConnection()
    {
        this("");
    }


    /**
     * Creates a StringConnection with the given input string
     * 
     * @param    str        Input stream contents
     */
    public StringConnection(String str)
    {
        sis = new StringInputStream(str);
        sos = new StringOutputStream();
    }

    //--------------------------------------------------------------------------
    //  IConnection Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see IConnection#open()
     */
    public void open()
    {
        // nothing to do
    }


    /**
     * @see IConnection#close()
     */
    public void close() throws IOException
    {
        // nothing to do
    }


    /**
     * @see IConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException
    {
        return sis;
    }


    /**
     * @see IConnection#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException
    {
        return sos;
    }

    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------
    
    /**
     * Returns output stream as a string
     * 
     * @return  String
     */
    public String getOutputString()
    {
        return sos.toString();
    }
}