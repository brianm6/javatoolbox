package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * String based implementation of an IConnection
 */
public class StringConnection extends AbstractConnection implements IConnection
{
    /** 
     * Input stream 
     */
    private StringInputStream sis_;

    /** 
     * Output stream 
     */
    private StringOutputStream sos_;
    
    /**
     * Connected flag
     */
    private boolean connected_;
    
    

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
        sis_ = new StringInputStream(str);
        sos_ = new StringOutputStream();
        
        connected_ = false;
    }

    //--------------------------------------------------------------------------
    //  IConnection interface
    //--------------------------------------------------------------------------
    
    /**
     * @see IConnection#open()
     */
    public void connect()
    {
        // nothing to do
        connected_ = true;
    }


    /**
     * @see IConnection#close()
     */
    public void close() throws IOException
    {
        // nothing to do
        connected_ = false;
    }


    /**
     * @see IConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException
    {
        return sis_;
    }


    /**
     * @see IConnection#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException
    {
        return sos_;
    }


    /**
     * @see IConnection#isConnected()
     */
    public boolean isConnected()
    {
        return connected_;
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
        return sos_.toString();
    }
}