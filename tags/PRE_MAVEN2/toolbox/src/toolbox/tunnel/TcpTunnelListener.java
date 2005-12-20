package toolbox.tunnel;

import toolbox.util.io.MonitoredOutputStream;

/**
 * Listener for events generated by a TcpTunnel.
 */
public interface TcpTunnelListener {
    
    /**
     * Notification that the internal status of the tunnel has changed.
     * 
     * @param tunnel TCP tunnel.
     * @param status New status.
     */
    void statusChanged(TcpTunnel tunnel, String status);
    
    
    /**
     * Notification of the number of bytes read over the last connection and
     * the number of bytes read over the life of the tunnel.
     * 
     * @param tunnel TCP tunnel.
     * @param connBytesRead Number of bytes read through the duration of the
     *        most recent connection.
     * @param totalBytesRead Total number of bytes read over the life of the
     *        tunnel.
     */
    void bytesRead(TcpTunnel tunnel, int connBytesRead, int totalBytesRead);

    
    /**
     * Notification of the number of bytes written over the last connection and
     * the number of bytes written over the life of the tunnel.
     * 
     * @param tunnel TCP tunnel.
     * @param connBytesWritten Number of bytes written through the duration 
     *        of the most recent connection.
     * @param totalBytesWritten Total number of bytes written over the life of
     *        the tunnel.
     */
    void bytesWritten(
        TcpTunnel tunnel,
        int connBytesWritten,
        int totalBytesWritten);
                         
    
    /**
     * Notification that the tunnel has been started successfully.
     * 
     * @param tunnel The tunnel that was started.
     */                             
    void tunnelStarted(TcpTunnel tunnel);
    
    
    /**
     * Notification that a new connection has been accepted by the tunnel. 
     * Data written to the new connection will be forwarded to the remote host.
     * Data read from the new connection will be forwarded to the client.
     * 
     * @param incomingSink Monitored incoming sink.
     * @param outgoingSink Monitored outgoing sink.
     */
    void newConnection(
        MonitoredOutputStream incomingSink, 
        MonitoredOutputStream outgoingSink);
}