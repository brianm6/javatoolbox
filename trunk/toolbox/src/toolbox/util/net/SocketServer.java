package toolbox.util.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import toolbox.util.SocketUtil;
import toolbox.util.concurrent.Mutex;
import toolbox.util.thread.ThreadDispatcher;
import toolbox.util.thread.strategy.ThreadPoolStrategy;


/**
 * Generic SocketServer implementation that supports 
 * <pre>
 * 
 * - pluggable connection handlers (anything that implements IConnectionHandler)
 * - sync/async dispatch of incoming connections to handlers
 * - dispatching of handlers is done via a configurable thread pool
 * 
 * </pre>
 */
public class SocketServer implements Runnable
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(SocketServer.class);

    /** 
     * Socket server configuration properties
     */
    private SocketServerConfig serverConfig_;

    /** 
     * Server socket 
     */
    private ServerSocket serverSocket_;

    /** 
     * Thread containing listening server socket 
     */
    private Thread serverThread_;

    /** 
     * Thead pool used to service connection handlers 
     */
    private ThreadDispatcher dispatcher_;

    /** 
     * Mutex used for coord between calling and server threads 
     */
    private Mutex startedMutex_;

    /** 
     * Exit variant for loop accepting incoming connections 
     */
    private boolean shutdown_ = false;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Create a SocketServer with the given server configuration
     * 
     * @param  newconfig    Socket server configuration
     */
    public SocketServer(SocketServerConfig newConfig)
    {
        serverConfig_ = newConfig;
    }

    //--------------------------------------------------------------------------
    //  Public Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts the socket server
     * 
     * @throws  IOExeption on I/O error
     */
    public void start() throws IOException
    {
        String method = "[start ] ";

        // reset shutdown flag
        shutdown_ = false;

        try
        {
            logger_.debug(method + "Socket server starting ...");

            /*
             * create new mutex and acquires it. the run() method will
             * release it just before doing accept(). At this
             * point, the end of this method will try to acquire()
             * it again. This will minimize the chance of a client
             * immediately getting refused a connection
             * to the server socket right after start() returns.
             */
            startedMutex_ = new Mutex();
            startedMutex_.acquire();

            // create the socket with listening port and queue size
            serverSocket_ = 
                new ServerSocket(
                    serverConfig_.getServerPort(), 
                    serverConfig_.getSocketQueueSize());

            // create thread that server socket will be spun off on
            serverThread_ = new Thread(this);

            // configure additional elements of the socket and thread
            configure();

            // start listening on a separate thread
            serverThread_.start();

            // wait to acquire mutex that is released in run() method
            startedMutex_.acquire();
        }
        catch (InterruptedException ie)
        {
            logger_.error(ie.getMessage(), ie);
        }
    }


    /** 
     * Stops the socket server
     * 
     * @throws  IOExeption on I/O error
     */
    public void stop() throws IOException
    {
        String method = "[stop  ] ";

        // set exit variant to at least try to shutdown gracefully
        shutdown_ = true;

        // TODO: more graceful way?
        serverSocket_.close();
        serverThread_.interrupt();
        logger_.debug(method + "Stopped socket server on port " + getServerPort());
    }


    /**
     * Default behavior is to ask the server config for a 
     * concrete instance of the connection handler. In this
     * case, the default returns the config specified connection
     * handler decorated by an async connection handler.
     * 
     * @return  IConnectionHandler
     */
    public IConnectionHandler getConnectionHandler()
    {
        return new AsyncConnectionHandler(
            serverConfig_.getConnectionHandler(), dispatcher_);
    }


    /**
     * Accessor for the port the server is running on
     * 
     * @return    int
     */
    public int getServerPort()
    {
        return serverSocket_.getLocalPort();
    }


    /**
     * Dumps SocketServer state to a string 
     * 
     * @return  String
     */
    public String toString()
    {
        String nl  = "\n";
        String nlt = nl + "\t";

        return "SocketServer" + nl + "{" + nlt + 
               "serverConfig = " + serverConfig_ + nlt + 
               "serverSocket = " + serverSocket_ + nlt + 
               "thread       = " + serverThread_ + nlt + 
               "publisher    = " + dispatcher_ + nlt + 
               "shutdown     = " + shutdown_ + nl + 
               "}";
    }


    //--------------------------------------------------------------------------
    //  Private Implementation
    //--------------------------------------------------------------------------

    /**
     * Configure the server with config info provided at time of construction
     * 
     * @throws  SocketException on socket error
     */
    private void configure() throws SocketException
    {
        // name thread based on server socket port
        serverThread_.setName("SockSvr-" + serverConfig_.getServerPort());
        serverThread_.setDaemon(true);
        serverSocket_.setSoTimeout(serverConfig_.getSocketTimeout());

        // create thread pool
        ThreadPoolStrategy strategy = 
            new ThreadPoolStrategy(
                serverConfig_.getActiveConnections(), 
                serverConfig_.getHandlerQueueSize());

        // init thread pool with strategy
        dispatcher_ = new ThreadDispatcher(strategy);
    }

    //--------------------------------------------------------------------------
    //  Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Called once socket server has a dedicated thread
     */
    public void run()
    {
        String method = "[run   ] ";
        
        logger_.debug(method + 
            "Waiting for connection on " + serverSocket_.getLocalPort());

        while (!shutdown_)
        {
            try
            {
                // release mutex so that start() can exit
                startedMutex_.release();

                // wait for a connection
                Socket socket = serverSocket_.accept();

                logger_.debug(method + "Accepted connection from " + 
                             socket.getInetAddress() + ":" + socket.getPort());
                
                // set timeout on newly acquired socket
                socket.setSoTimeout(serverConfig_.getSocketTimeout());

                // wrap socket in a connection
                IConnection socketConn = new SocketConnection(socket);

                // create appropriate handler
                IConnectionHandler handler = getConnectionHandler();

                // handle the connection
                handler.handle(socketConn);

                // on to the next accept...
            }
            catch (SocketException se)
            {
                // If socket closed exception, ignore because it occurs 
                // in shutdown process
                if (!SocketUtil.isReasonSocketClosed(se))
                    logger_.error(this, se);
            }
            catch (InterruptedIOException iioe)
            {
                // if accept times out, just ignore and 
                // let it loop around again
                if (!SocketUtil.isReasonAcceptTimeout(iioe))
                    logger_.error(this, iioe);
            }
            catch (IOException e)
            {
                logger_.error(this, e);
            }
        }
    }
}
