package toolbox.util.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.SocketUtil;
import toolbox.util.concurrent.Mutex;
import toolbox.util.thread.ThreadDispatcher;
import toolbox.util.thread.strategy.ThreadPoolStrategy;
import toolbox.util.thread.strategy.ThreadedDispatcherStrategy;

/**
 * Simple SocketServer implementation with conveniences built it.  
 * <p>
 * Features include:
 * <ul>
 *   <li>Pluggable connection handlers
 *   <li>Sync/async dispatch of incoming connections to handlers
 *   <li>Handlers are pooled and configurable for efficiency and flexibility
 * </ul>
 */
public class SocketServer implements Runnable
{
    private static final Logger logger_ = 
        Logger.getLogger(SocketServer.class);

    /** 
     * Server configuration contains info such as server port, timeout, etc. 
     */
    private SocketServerConfig config_;

    /** 
     * Lower level server socket delegate. 
     */
    private ServerSocket serverSocket_;

    /** 
     * Thread of execution that the server socket accept()'s on.  
     */
    private Thread acceptThread_;

    /** 
     * Thead pool used to service socket clients.
     */
    private ThreadDispatcher dispatcher_;

    /** 
     * Mutex used at startup to reduce likelyhood of a race condition. 
     */
    private Mutex startedMutex_;

    /** 
     * Shutdown flag. 
     */
    private boolean shutdown_ = false;

    /** 
     * List of socket server listeners. 
     */
    private ISocketServerListener[] listeners_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a SocketServer with the given server configuration
     * 
     * @param newConfig Server configuration
     */
    public SocketServer(SocketServerConfig newConfig)
    {
        config_ = newConfig;
        listeners_ = new ISocketServerListener[0];
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Starts the socket server.
     * 
     * @throws IOException on I/O error
     */
    public void start() throws IOException
    {
        // Reset shutdown flag
        shutdown_ = false;

        try
        {
            logger_.debug(config_.getName() + " socket server starting ...");

            /*
             * create new mutex and acquires it. the run() method will release 
             * it just before doing accept(). At this point, the end of this 
             * method will try to acquire() it again. This will minimize the 
             * chance of a client immediately getting refused a connection to 
             * the server socket right after start() returns.
             */
            startedMutex_ = new Mutex();
            startedMutex_.acquire();

            // Create the socket with listening port and queue size 
            serverSocket_ = 
                new ServerSocket(
                    config_.getServerPort(),
                    config_.getSocketQueueSize());

            // Create thread that server socket will be spun off on
            acceptThread_ = new Thread(this);

            // Configure additional elements of the socket and thread
            configure();

            // Start listening on a separate thread
            acceptThread_.start();

            // Wait to acquire mutex that is released in run() method
            startedMutex_.acquire();
        }
        catch (InterruptedException ie)
        {
            logger_.error(ie);
        }
    }


    /** 
     * Stops the socket server.
     * 
     * @throws IOException on I/O error
     */
    public void stop() throws IOException
    {
        // Set exit variant to at least try to shutdown gracefully
        shutdown_ = true;
        
        dispatcher_.shutdown();
        dispatcher_ = null;
        
        serverSocket_.close();
        acceptThread_.interrupt();
        logger_.info("Stopped socket server on port " + getServerPort());
    }


    /**
     * Default behavior is to ask the server config for a concrete instance of 
     * the connection handler. In this case, the default returns the config 
     * specified connection handler decorated by an async connection handler.
     * 
     * @return IConnectionHandler
     */
    public IConnectionHandler getConnectionHandler()
    {
        return new AsyncConnectionHandler(
            config_.getConnectionHandler(), dispatcher_);
    }


    /**
     * Accessor for the port the server is running on.
     * 
     * @return Server port number
     */
    public int getServerPort()
    {
        return serverSocket_.getLocalPort();
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Configure the server with config info provided at time of construction.
     * 
     * @throws SocketException When socket error occurs
     */
    private void configure() throws SocketException
    {
        // Name thread based on server socket port
        acceptThread_.setName("SocketServer-" + 
            config_.getName() + ":" + config_.getServerPort());
            
        acceptThread_.setDaemon(true);
        serverSocket_.setSoTimeout(config_.getSocketTimeout());

        // Create thread pool
        ThreadedDispatcherStrategy strategy = 
            new ThreadPoolStrategy(config_.getActiveConnections(), 
                config_.getHandlerQueueSize());

        // Init thread pool with strategy
        dispatcher_ = new ThreadDispatcher(strategy);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Dumps SocketServer state to a string. 
     * 
     * @return String
     */
    public String toString()
    {
        String nl  = "\n";
        String nlt = nl + "\t";

        return "SocketServer" + nl + "{" + nlt + 
               "serverConfig = " + config_ + nlt + 
               "serverSocket = " + serverSocket_ + nlt + 
               "thread       = " + acceptThread_ + nlt + 
               "publisher    = " + dispatcher_ + nlt + 
               "shutdown     = " + shutdown_ + nl + 
               "}";
    }

    //--------------------------------------------------------------------------
    // Listener support
    //--------------------------------------------------------------------------

    /**
     * Fires notification that a new socket client was just accepted.
     * 
     * @param socket New socket that was created
     */
    protected void fireSocketAccepted(Socket socket, IConnection conn)
    {
        for (int i=0; i<listeners_.length; 
            listeners_[i++].socketAccepted(socket, conn));
    }


    /**
     * Fires notification that the server has started and ready to accept
     * client connections.
     */
    protected void fireServerStarted()
    {
        for (int i=0; i<listeners_.length; listeners_[i++].serverStarted(this));
    }


    /**
     * Adds a listener to the socket server.
     * 
     * @param listener Implementor of ISocketServerListener
     */
    public void addSocketServerListener(ISocketServerListener listener)
    {
        listeners_ = 
            (ISocketServerListener[]) ArrayUtil.add(listeners_, listener);
    }
    
    
    /**
     * Removes a listener from the socket server.
     * 
     * @param listener Implementor of ISocketServerListener
     */
    public void removeSocketServerListener(ISocketServerListener listener)
    {
        listeners_ = 
            (ISocketServerListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    //--------------------------------------------------------------------------
    //  Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Thread for socket.accept()
     */
    public void run()
    {
        logger_.info(
            config_.getName() + 
            " waiting for connection on " + 
            serverSocket_.getLocalPort());

        fireServerStarted();
        
        while (!shutdown_)
        {
            try
            {
                // Release mutex so that start() can exit 
                startedMutex_.release();

                // Wait for a connection
                Socket socket = serverSocket_.accept();

                logger_.debug(
                    config_.getName() + " accepted connection from " + 
                        socket.getInetAddress() + ":" + socket.getPort());
                
                // Set timeout on newly acquired socket
                socket.setSoTimeout(config_.getSocketTimeout());

                // Wrap socket in a connection
                IConnection socketConn = new SocketConnection(socket);

                // Fire notification to listeners                             
                fireSocketAccepted(socket, socketConn);                             

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
                    logger_.error("run", se);
            }
            catch (InterruptedIOException iioe)
            {
                // If accept times out, just ignore and 
                // let it loop around again
                
                if (!SocketUtil.isReasonAcceptTimeout(iioe))
                    logger_.error("run", iioe);
            }
            catch (IOException e)
            {
                logger_.error("run", e);
            }
        }
    }
}