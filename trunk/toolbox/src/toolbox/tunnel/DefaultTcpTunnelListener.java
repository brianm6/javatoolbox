package toolbox.tunnel;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import toolbox.util.io.MonitoredOutputStream;

/**
 * Default implementation of the TcpTunnelListener Interface that adds
 * convenience methods to receive events in a blocking manner.
 */
public class DefaultTcpTunnelListener implements TcpTunnelListener {

    private static final Logger logger_ = 
        Logger.getLogger(DefaultTcpTunnelListener.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Queue for started events.
     */
    private BlockingQueue started_;

    /**
     * Total number of bytes written to the tunnel.
     */
    private int totalBytesWritten_;

    /**
     * Total number of bytes read by the tunnel.
     */
    private int totalBytesRead_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a DefaultTcpTunnelListener.
     */
    public DefaultTcpTunnelListener() {
        started_ = new LinkedBlockingQueue();
    }

    // --------------------------------------------------------------------------
    // TcpTunnelListener Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.tunnel.TcpTunnelListener#statusChanged(toolbox.tunnel.TcpTunnel, java.lang.String)
     */
    public void statusChanged(TcpTunnel tunnel, String status) {
    }


    /*
     * @see toolbox.tunnel.TcpTunnelListener#bytesRead(toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesRead(
        TcpTunnel tunnel,
        int connBytesRead,
        int totalBytesRead) {
        // logger_.debug("[bytesRead]" + connBytesRead + " " + totalBytesRead);
        totalBytesRead_ = totalBytesRead;
    }


    /*
     * @see toolbox.tunnel.TcpTunnelListener#bytesWritten(toolbox.tunnel.TcpTunnel, int, int)
     */
    public void bytesWritten(
        TcpTunnel tunnel,
        int connBytesWritten,
        int totalBytesWritten) {
        // logger_.debug("[bytesWritten]" + connBytesWritten + " " +
        // totalBytesWritten);
        totalBytesWritten_ = totalBytesWritten;
    }


    /*
     * @see toolbox.tunnel.TcpTunnelListener#tunnelStarted(toolbox.tunnel.TcpTunnel)
     */
    public void tunnelStarted(TcpTunnel tunnel) {
        try {
            started_.put(tunnel);
        }
        catch (InterruptedException e) {
            logger_.error(e);
        }
    }

    /*
     * @see toolbox.tunnel.TcpTunnelListener#newConnection(toolbox.util.io.MonitoredOutputStream, toolbox.util.io.MonitoredOutputStream)
     */
    public void newConnection(
        MonitoredOutputStream incomingSink,
        MonitoredOutputStream outgoingSink) {
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Blocks indefinitely until a started event is received.
     * 
     * @return TcpTunnel after it has been started.
     * @throws InterruptedException if interrupted.
     */
    public TcpTunnel waitForStarted() throws InterruptedException {
        return (TcpTunnel) started_.take();
    }


    /**
     * Returns the totalBytesRead.
     * 
     * @return int
     */
    public int getTotalBytesRead() {
        return totalBytesRead_;
    }


    /**
     * Returns the totalBytesWritten.
     * 
     * @return int
     */
    public int getTotalBytesWritten() {
        return totalBytesWritten_;
    }
}