package toolbox.util.net;

/**
 * IConnectionListener provides the notification interface for various events
 * generated by an IConnection. This includes:
 * <p>
 * <ul>
 *   <li>Connection about to close
 *   <li>Connection closed
 *   <li>Connection interrupted
 *   <li>Connection started
 * </ul>
 */
public interface IConnectionListener
{
    /**
     * Notification that a connection is about to be closed.
     * 
     * @param connection Connection about to be closed.
     */
    void connectionClosing(IConnection connection);
    
    
    /**
     * Notification that a connection has been closed.
     * 
     * @param connection Connection that was closed.
     */
    void connectionClosed(IConnection connection);
    
    
    /**
     * Notification that a connection was interrupted.
     * 
     * @param connection Connection that was interrupted.
     */
    void connectionInterrupted(IConnection connection);
    
    
    /**
     * Notification that a connection was established/connected/started.
     * 
     * @param connection Connection that was started.
     */
    void connectionStarted(IConnection connection);
}