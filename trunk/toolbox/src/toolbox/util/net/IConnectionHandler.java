package toolbox.util.net;

/**
 * A connection handler is created by a SocketServer to service an incoming
 * socket client after a connection has been established.
 * <ul>
 *  <li>The connection handler type is specified in the SocketServerConfig.
 *  <li>A connection handler is responsible for the conversation that takes 
 *      place between itself and a socket client.
 *  <li>A connection handler is tasked with terminating the connection when the
 *      conversation reaches a stopping point.
 *  <li>A connection handler can choose to be full duplex or half duplex based
 *      on the needs of the protocol.
 * </ul>
 */
public interface IConnectionHandler
{
    /**
     * Handles a new connection.
     * 
     * @param conn Connection to service.
     * @return An arbitrary object as specified by the implementor.
     */
    Object handle(IConnection conn);
}