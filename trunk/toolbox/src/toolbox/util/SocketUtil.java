package toolbox.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * Socket related utilities
 */
public class SocketUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(SocketUtil.class);

    /** 
     * Value embedded in message for an accept() timeout 
     */
    public static final String MSG_ACCEPT_TIMEOUT = "Accept timed out";

    /** 
     * Value embedded in exception message for a socket closed exception 
     */
    public static final String MSG_SOCKET_CLOSED = "Socket closed";


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction
     */
    private SocketUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
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

        // Zero max retries = infinite 
        if (maxRetries <= 0)
            maxRetries = Integer.MAX_VALUE;

        // Loop max retries times
        for (int i = 0; i < maxRetries; i++)
        {
            try
            {
                // If successful, break
                socket = new Socket(hostname, port);
                break;
            }
            catch (ConnectException e)
            {
                logger_.debug("Connect to " + hostname + ":" + port + 
                    " failed. Will retry in " + interval + " secs " + 
                    (maxRetries - i) + " times..");

                // Else sleep and try again
                ThreadUtil.sleep(interval * 1000);
            }
        }

        return socket;
    }


    /**
     * Determines if the reason for an InterruptedIOException is the timeout
     * of the call to socket.accept()
     * 
     * <pre>
     * NOTE: This is crude, but there is no otherway to execute conditional
     *       logic based on this exception.
     * </pre>
     * 
     * @param  iioe    Exception to check
     * @return True if caused by an accept timeout, false otherwise
     */
    public static boolean isReasonAcceptTimeout(InterruptedIOException iioe)
    {
        return iioe.getMessage().equals(MSG_ACCEPT_TIMEOUT);
    }


    /**
     * Determines if the reason for a SocketException is because the socket
     * has already been closed.
     * 
     * <pre>
     * NOTE: This is crude, but there is no otherway to execute conditional
     *       logic based on this exception.
     * </pre>
     * 
     * @param  se   SocketException to check
     * @return True if exception raised by a closed socket , false otherwise
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