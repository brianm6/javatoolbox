package toolbox.util.net;

import java.net.Socket;

/**
 * Interface that defines the events generates by a {@link SocketServer}.
 */
public interface ISocketServerListener
{
    /**
     * Notification that a client socket connection was accepted.
     * 
     * @param socket Newly created socket to communicate with the client.
     * @param connection Resulting connection that was created. 
     */
    public void socketAccepted(Socket socket, IConnection connection);


    /**
     * Notification that the socket server has started and is ready to accept
     * client connections.
     * 
     * @param server Server that started up
     */    
    public void serverStarted(SocketServer server);
}
