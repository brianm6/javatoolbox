package toolbox.tunnel;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;

/**
 * Default implementation of the TcpTunnelListener Interface that adds 
 * convenience methods to receive events in a blocking manner.
 */
public class DefaultTcpTunnelListener implements TcpTunnelListener
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultTcpTunnelListener.class);
    
    /**
     * Queue for started events
     */    
    private BlockingQueue started_ = new BlockingQueue();

    //--------------------------------------------------------------------------
    // TcpTunnelListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.tunnel.TcpTunnelListener#statusChanged(
     *          toolbox.tunnel.TcpTunnel, java.lang.String)
     */
    public void statusChanged(TcpTunnel tunnel, String status)
    {
    }

    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesRead(
     *          toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesRead(TcpTunnel tunnel, int connBytesRead, 
        int totalBytesRead)
    {
    }

    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(
     *          toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesWritten(TcpTunnel tunnel, int connBytesWritten,
        int totalBytesWritten)
    {
    }
    
    /**
     * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(
     *          toolbox.tunnel.TcpTunnel)
     */
    public void tunnelStarted(TcpTunnel tunnel)
    {
        started_.push(tunnel);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Blocks indefinitely until a started event is received.
     * 
     * @return TcpTunnel after it has been started
     * @throws InterruptedException if interrupted
     */
    public TcpTunnel waitForStarted() throws InterruptedException
    {
        return (TcpTunnel) started_.pull();
    }
}
