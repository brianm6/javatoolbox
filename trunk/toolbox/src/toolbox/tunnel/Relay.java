package toolbox.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.StreamUtil;

/**
 * A Relay transfers bytes from an InputStream to an OutputStream
 * asynchronously.
 * 
 * @see TcpTunnel
 */
public class Relay implements Runnable
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Size of data window. 
     */
    private static final int BUFFER_SIZE = 1024;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * InputStream data is read from. 
     */    
    private InputStream in_;
    
    /** 
     * OutputStream data is forwarded to. 
     */
    private OutputStream out_;
    
    /** 
     * Number of bytes transferred. 
     */
    private int count_;
    
    /** 
     * Copy buffer. 
     */    
    private byte[] buffer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new relay between an input and output stream.
     * 
     * @param in Input stream to read bytes from.
     * @param out Output stream to write bytes to.
     */
    public Relay(InputStream in, OutputStream out)
    {
        in_     = in;
        out_    = out;
        count_  = 0;
        buffer_ = new byte[BUFFER_SIZE];
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the relay.
     */
    public void run()
    {
        int n;

        try
        {
            while ((n = in_.read(buffer_)) > 0)
            {
                out_.write(buffer_, 0, n);
                out_.flush();
                count_ += n;
            }
        }
        catch (IOException e)
        {
            ; // Ignore
        }
        finally
        {
            StreamUtil.close(in_);
            StreamUtil.close(out_);
        }
    }
}