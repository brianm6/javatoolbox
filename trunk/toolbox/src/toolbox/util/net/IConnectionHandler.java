package toolbox.util.net;

/**
 * A connection handler is created by a SocketServer to service an incoming
 * socket client after a connection has been established.<br>
 * The connection handler type is specified in the SocketServerConfig<br>
 * A connection handler is responsible for the conversation that takes place
 * between itself and a socket client.<br>
 * A connection handler is tasked with terminating the connection when the
 * conversation reaches a stopping point.<br>
 * A connection handler can choose to be full duplex or half duplex based on 
 * the needs of the protocol.<br>
 */
public interface IConnectionHandler
{
    /**
     * Handles a new connection.
     * 
     * @param conn Connection to service
     * @return An arbitrary object as specified by the implementor
     */
    public Object handle(IConnection conn);
}