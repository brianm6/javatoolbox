package toolbox.util.net;

/**
 * Listener interface for IConnection
 */
public interface IConnectionListener
{
    /**
     * Notification that a connection is about to be closed
     * 
     * @param  connection  Connection about to be closed
     */
    public void connectionClosing(IConnection connection);
    
    /**
     * Notification that a connection has been closed
     * 
     * @param  connection   Connection that was closed
     */
    public void connectionClosed(IConnection connection);
    
    /**
     * Notification that a connection was interrupted
     * 
     * @param  connection  Connection that was interrupted
     */
    public void connectionInterrupted(IConnection connection);
    
    /**
     * Notification that a connection was started
     * 
     * @param  connection  Connection that was started
     */
    public void connectionStarted(IConnection connection);
}