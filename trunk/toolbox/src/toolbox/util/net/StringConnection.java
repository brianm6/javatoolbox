package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * String based implementation of an {@link IConnection}.
 */
public class StringConnection extends AbstractConnection implements IConnection
{
    /** Input stream */
    private StringInputStream sis_;

    /** Output stream */
    private StringOutputStream sos_;
    
    /** Connected flag */
    private boolean connected_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public StringConnection()
    {
        this("");
    }

    /**
     * Creates a StringConnection with the given input string
     * 
     * @param  str  Input stream contents
     */
    public StringConnection(String str)
    {
        sis_ = new StringInputStream(str);
        sos_ = new StringOutputStream();
        
        connected_ = false;
    }

    //--------------------------------------------------------------------------
    //  IConnection Interface
    //--------------------------------------------------------------------------
    
    public void connect()
    {
        connected_ = true;
    }

    public void close() throws IOException
    {
        connected_ = false;
    }

    public InputStream getInputStream() throws IOException
    {
        return sis_;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return sos_;
    }

    public boolean isConnected()
    {
        return connected_;
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * @return Output stream as a string
     */
    public String getOutputString()
    {
        return sos_.toString();
    }
}