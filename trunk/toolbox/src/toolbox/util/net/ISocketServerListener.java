package toolbox.util.net;

import java.net.Socket;

/**
 * Listener for socket server
 */
public interface ISocketServerListener
{
    /**
     * Notication that a client socket connection was accepted
     * 
     * @param  socket  Newly created socket to communicate with the client
     */
    public void socketAccepted(Socket socket);
}
