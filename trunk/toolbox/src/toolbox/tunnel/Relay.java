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
    private static final int BUFSIZ = 1024;
    
    private InputStream   in_;
    private OutputStream  out_;
    private byte[]        buf_ = new byte[BUFSIZ];

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new relay
     * 
     * @param  in   Input stream
     * @param  out  Output stream
     */
    public Relay(InputStream in, OutputStream out)
    {
        in_ = in;
        out_ = out;
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
            while ((n = in_.read(buf_)) > 0)
            {
                out_.write(buf_, 0, n);
                out_.flush();
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