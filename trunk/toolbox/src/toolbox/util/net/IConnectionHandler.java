package toolbox.util.net;

/**
 * Generic interface for a connection handler
 */
public interface IConnectionHandler
{
    /**
     * Handles the connection
     * 
     * @param    conn    IConnection
     * @return   An arbitrary object as specified by the implementor
     */
    public Object handle(IConnection conn);
}