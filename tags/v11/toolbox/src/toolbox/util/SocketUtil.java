package toolbox.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Category;

/**
 * Socket related utilities
 */
public class SocketUtil
{
    /** Logger **/
    private static final Category logger = 
        Category.getInstance(SocketUtil.class);

    /** Value embedded in message for an accept() timeout **/
    public static final String MSG_ACCEPT_TIMEOUT = "Accept timed out";

    /** Value embedded in exception message for a socket closed exception **/
    public static final String MSG_SOCKET_CLOSED = "Socket closed";


    /**
     * Prevent construction
     */
    private SocketUtil()
    {
    }


    /**
     * Establishes a connection to a server socket with a retry strategy
     * 
     * @param   hostname      Host to connect to
     * @param   port          Port to connect to
     * @param   interval      Retry interval in seconds. Zero = no interval
     * @param   maxRetries    Maximum number of times to retry. Zero = infinite
     * @return  Socket if connection succeeded, null otherwise
     * @throws  IOException if an error occurs
     * @throws  UnknownHostException if host not found/resolvable
     */
    public static Socket connectWithRetry(String hostname, int port, 
        int interval, int maxRetries) throws IOException, UnknownHostException
    {
        Socket socket = null;

        /* zero max retries = infinite */
        if (maxRetries <= 0)
            maxRetries = Integer.MAX_VALUE;

        /* loop max retries */
        for (int i = 0; i < maxRetries; i++)
        {
            try
            {
                /* if successful, break */
                socket = new Socket(hostname, port);
                break;
            }
            catch (ConnectException e)
            {
                logger.debug("Connect to " + hostname + ":" + port + 
                    " failed. Will retry in " + interval + " secs " + 
                    (maxRetries - i) + " times..");

                /* else sleep and try again */
                ThreadUtil.sleep(interval * 1000);
            }
        }

        return socket;
    }


    /**
     * Determines if the reason for an InterruptedIOException is the timeout
     * of the call to socket.accept()
     * 
     * @param  iioe    Exception to check
     * @return True if caused by an accept timeout, false otherwise
     * 
     * NOTE: This is crude, but there is no otherway to execute conditional
     *       logic based on this exception.
     */
    public static boolean isReasonAcceptTimeout(InterruptedIOException iioe)
    {
        return iioe.getMessage().equals(MSG_ACCEPT_TIMEOUT);
    }


    /**
     * Determines if the reason for a SocketException is because the socket
     * has already been closed.
     * 
     * @param  se   SocketException to check
     * @return True if exception raised by a closed socket , false otherwise
     * 
     * NOTE: This is crude, but there is no otherway to execute conditional
     *       logic based on this exception.
     */
    public static boolean isReasonSocketClosed(SocketException se)
    {
        return se.getMessage().equalsIgnoreCase(MSG_SOCKET_CLOSED);
    }

    
    /**
     * Returns a "free" port on the local host which is guaranteed not to be
     * occupied by existing services.
     * 
     * @return  Port number
     * @throws  IOException on IO error
     */
    public static int getFreePort() throws IOException
    {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }
}