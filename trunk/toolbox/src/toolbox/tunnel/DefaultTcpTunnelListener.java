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

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Queue for started events.
     */
    private BlockingQueue started_ = new BlockingQueue();

    /**
     * Total numver of bytes written through the tunnel.
     */
    private int totalBytesWritten_;

    /**
     * Total number of bytes read through the tunnel.
     */
    private int totalBytesRead_;

    //--------------------------------------------------------------------------
    // TcpTunnelListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.tunnel.TcpTunnelListener#statusChanged(
     *      toolbox.tunnel.TcpTunnel, java.lang.String)
     */
    public void statusChanged(TcpTunnel tunnel, String status)
    {
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesRead(
     *      toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesRead(TcpTunnel tunnel, int connBytesRead,
        int totalBytesRead)
    {
        //logger_.debug("[bytesRead]" + connBytesRead + " " + totalBytesRead);
        totalBytesRead_ = totalBytesRead;
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(
     *      toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesWritten(TcpTunnel tunnel, int connBytesWritten,
        int totalBytesWritten)
    {
        //logger_.debug("[bytesWritten]" + connBytesWritten + " " + totalBytesWritten);
        totalBytesWritten_ = totalBytesWritten;
    }


    /**
     * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(
     *      toolbox.tunnel.TcpTunnel)
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
     * @return TcpTunnel after it has been started.
     * @throws InterruptedException if interrupted.
     */
    public TcpTunnel waitForStarted() throws InterruptedException
    {
        return (TcpTunnel) started_.pull();
    }


    /**
     * Returns the totalBytesRead.
     *
     * @return int
     */
    public int getTotalBytesRead()
    {
        return totalBytesRead_;
    }


    /**
     * Returns the totalBytesWritten.
     *
     * @return int
     */
    public int getTotalBytesWritten()
    {
        return totalBytesWritten_;
    }
}