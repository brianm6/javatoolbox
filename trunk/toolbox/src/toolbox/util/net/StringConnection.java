package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * String based implementation of an {@link IConnection}.<br>Very useful for 
 * unit testing when a socket based implementation is too much work.
 */
public class StringConnection extends AbstractConnection implements IConnection
{
    /** 
     * Internal string based InputStream. 
     */
    private StringInputStream sis_;

    /** 
     * Internal string based OutputStream. 
     */
    private StringOutputStream sos_;
    
    /** 
     * Connected flag.
     */
    private boolean connected_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a StringConnection.
     */
    public StringConnection()
    {
        this("");
    }


    /**
     * Creates a StringConnection with the given input string.
     * 
     * @param str Input stream contents
     */
    public StringConnection(String str)
    {
        sis_ = new StringInputStream(str);
        sos_ = new StringOutputStream();
        connected_ = false;
    }

    //--------------------------------------------------------------------------
    // IConnection Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.net.IConnection#connect()
     */
    public void connect()
    {
        connected_ = true;
    }


    /**
     * @see toolbox.util.net.IConnection#close()
     */
    public void close() throws IOException
    {
        connected_ = false;
    }


    /**
     * @see toolbox.util.net.IConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException
    {
        return sis_;
    }


    /**
     * @see toolbox.util.net.IConnection#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException
    {
        return sos_;
    }


    /**
     * @see toolbox.util.net.IConnection#isConnected()
     */
    public boolean isConnected()
    {
        return connected_;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the contents of the output stream as a string.
     * 
     * @return String
     */
    public String getOutputString()
    {
        return sos_.toString();
    }
}