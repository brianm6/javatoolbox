package toolbox.util.net;

import java.net.Socket;

/**
 * Listener for socket server
 */
public interface ISocketServerListener
{
    public void socketAccepted(Socket socket);
}
