package toolbox.util.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.SocketUtil;
import toolbox.util.concurrent.Mutex;
import toolbox.util.thread.ThreadDispatcher;
import toolbox.util.thread.strategy.ThreadPoolStrategy;
import toolbox.util.thread.strategy.ThreadedDispatcherStrategy;

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
    /** 
     * Logger 
     */
    private static final Logger logger_ = 
        Logger.getLogger(SocketServer.class);

    /** 
     * Server configuration 
     */
    private SocketServerConfig config_;

    /** 
     * Server socket 
     */
    private ServerSocket serverSocket_;

    /** 
     * Thread of execution for the server socket accept()
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

    /** 
     * List of socket server listeners 
     */
    private List listeners_ = new ArrayList();

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a SocketServer with the given server configuration
     * 
     * @param  newConfig    Server configuration
     */
    public SocketServer(SocketServerConfig newConfig)
    {
        config_ = newConfig;
    }

    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------
    
    /**
     * Starts the socket server
     * 
     * @throws IOException on I/O error
     */
    public void start() throws IOException
    {
        String method = "[start ] ";

        // Reset shutdown flag
        shutdown_ = false;

        try
        {
            logger_.debug(method + 
                config_.getName() + " socket server starting ...");

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

            // Create the socket with listening port and queue size 
            serverSocket_ = 
                new ServerSocket(
                    config_.getServerPort(),
                    config_.getSocketQueueSize());

            // Create thread that server socket will be spun off on
            serverThread_ = new Thread(this);

            // Configure additional elements of the socket and thread
            configure();

            // Start listening on a separate thread
            serverThread_.start();

            // Wait to acquire mutex that is released in run() method
            startedMutex_.acquire();
        }
        catch (InterruptedException ie)
        {
            logger_.error(method, ie);
        }
    }


    /** 
     * Stops the socket server
     * 
     * @throws IOException on I/O error
     */
    public void stop() throws IOException
    {
        String method = "[stop  ] ";

        // Set exit variant to at least try to shutdown gracefully
        shutdown_ = true;

        serverSocket_.close();
        serverThread_.interrupt();
        logger_.info(method + 
            "Stopped socket server on port " + getServerPort());
    }


    /**
     * Configure the server with config info provided at time of construction
     * 
     * @throws  SocketException  When socket error occurs
     */
    private void configure() throws SocketException
    {
        // Name thread based on server socket port
        serverThread_.setName(
            config_.getName() + ":" + config_.getServerPort());
            
        serverThread_.setDaemon(true);
        serverSocket_.setSoTimeout(config_.getSocketTimeout());

        // Create thread pool
        ThreadedDispatcherStrategy strategy = 
            new ThreadPoolStrategy(config_.getActiveConnections(), 
                config_.getHandlerQueueSize());

        // Init thread pool with strategy
        dispatcher_ = new ThreadDispatcher(strategy);
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
            config_.getConnectionHandler(), dispatcher_);
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
               "serverConfig = " + config_ + nlt + 
               "serverSocket = " + serverSocket_ + nlt + 
               "thread       = " + serverThread_ + nlt + 
               "publisher    = " + dispatcher_ + nlt + 
               "shutdown     = " + shutdown_ + nl + 
               "}";
    }

    //--------------------------------------------------------------------------
    //  Listener support
    //--------------------------------------------------------------------------

    /**
     * Fires notification that a new socket client was just accepted
     * 
     * @param  socket  New socket that was created
     */
    protected void fireSocketAccepted(Socket socket)
    {
        for (Iterator i = listeners_.iterator(); i.hasNext(); )
            ((ISocketServerListener)i.next()).socketAccepted(socket);
    }

    /**
     * Adds a listener to the socket server
     * 
     * @param  listener  Implementor of ISocketServerListener
     */
    public void addSocketServerListener(ISocketServerListener listener)
    {
        listeners_.add(listener);
    }
    
    /**
     * Removes a listener from the socket server
     * 
     * @param  listener  Implementor of ISocketServerListener
     */
    public void removeSocketServerListener(ISocketServerListener listener)
    {
        listeners_.remove(listener);
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
        
        logger_.info(method + config_.getName() +
             " waiting for connection on " + serverSocket_.getLocalPort());

        while (!shutdown_)
        {
            try
            {
                // Release mutex so that start() can exit 
                startedMutex_.release();

                // Wait for a connection
                Socket socket = serverSocket_.accept();

                logger_.debug(method + 
                    config_.getName() + " accepted connection from " + 
                    socket.getInetAddress() + ":" + socket.getPort());

                // Fire notification to listeners                             
                fireSocketAccepted(socket);                             
                
                // Set timeout on newly acquired socket
                socket.setSoTimeout(config_.getSocketTimeout());

                // Wrap socket in a connection
                IConnection socketConn = new SocketConnection(socket);

                // Create handler 
                IConnectionHandler handler = getConnectionHandler();

                // Handle the connection
                handler.handle(socketConn);

                // On to the next accept...
            }
            catch (SocketException se)
            {
                // If socket closed exception, ignore because 
                // it occurs in shutdown process
                if (!SocketUtil.isReasonSocketClosed(se))
                    logger_.error(method, se);
            }
            catch (InterruptedIOException iioe)
            {
                // If accept times out, just ignore and 
                // let it loop around again
                if (!SocketUtil.isReasonAcceptTimeout(iioe))
                    logger_.error(method, iioe);
            }
            catch (IOException e)
            {
                logger_.error(method, e);
            }
        }
    }
}