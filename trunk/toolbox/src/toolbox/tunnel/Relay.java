package toolbox.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.ResourceCloser;

/**
 * A Relay object is used by TcpTunnel and JTcpTunnel to relay bytes from an
 * InputStream to a OutputStream.
 */
public class Relay implements Runnable
{
    private static final int BUFFER_SIZE = 1024;
    
    private InputStream   in_;
    private OutputStream  out_;
    private int           count_;    
    private byte[]        buffer_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new relay between an input and output stream
     * 
     * @param  in   Input stream to read bytes from
     * @param  out  Output stream to write bytes to
     */
    public Relay(InputStream in, OutputStream out)
    {
        in_     = in;
        out_    = out;
        count_  = 0;
        buffer_ = new byte[BUFFER_SIZE];
    }

    //--------------------------------------------------------------------------
    //  Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the relay
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
            // Ignore
        }
        finally
        {
            ResourceCloser.close(in_);
            ResourceCloser.close(out_);
        }
    }
}