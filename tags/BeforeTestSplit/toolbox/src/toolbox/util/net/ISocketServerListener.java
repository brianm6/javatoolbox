package toolbox.util.net;

import java.net.Socket;

/**
 * Interface that defines the events generates by a {@link SocketServer}.
 */
public interface ISocketServerListener
{
    /**
     * Notification that the socket server has started and is ready to accept
     * client connections.
     * 
     * @param server Server that started up.
     */    
    void serverStarted(SocketServer server);
    
    
    /**
     * Notification that a client socket connection was accepted.
     * 
     * @param socket Newly created socket to communicate with the client.
     * @param connection Resulting connection that was created. 
     */
    void socketAccepted(Socket socket, IConnection connection);


    /**
     * Notification that the a client request has been serviced by the given
     * IConnectionHandler.
     * 
     * @param connectionHandler Connection handler.
     */
    void connectionHandled(IConnectionHandler connectionHandler);
    
    
    /**
     * Notification that the socket server has stopped.
     * 
     * @param server Server that stopped.
     */    
    void serverStopped(SocketServer server);
}
