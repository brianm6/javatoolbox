package toolbox.tunnel;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;

/**
 * Default implementation of the TcpTunnelListener Interface
 */
public class DefaultTcpTunnelListener implements TcpTunnelListener
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultTcpTunnelListener.class);
        
    private BlockingQueue started_ = new BlockingQueue();

    //--------------------------------------------------------------------------
    // TcpTunnelListener Interface
    //--------------------------------------------------------------------------
        
    public void statusChanged(TcpTunnel tunnel, String status)
    {
    }

    public void bytesRead(TcpTunnel tunnel, int connBytesRead, 
        int totalBytesRead)
    {
    }

    public void bytesWritten(TcpTunnel tunnel, int connBytesWritten,
        int totalBytesWritten)
    {
    }
    
    public void tunnelStarted(TcpTunnel tunnel)
    {
        try
        {
            started_.push(tunnel);
        }
        catch (InterruptedException ie)
        {
            logger_.error("tunnelStarted", ie);
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return TcpTunnel after it has been started
     * @throws InterruptedException if interrupted
     */
    public TcpTunnel waitForStarted() throws InterruptedException
    {
        return (TcpTunnel) started_.pull();
    }
}
