package toolbox.plugin.netmeter;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import toolbox.util.net.AsyncConnectionHandler;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;
import toolbox.util.net.ISocketServerListener;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;
import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;

/**
 * Server is a non-UI component that is used to collect data throughput
 * statistics when connected to by a Client.
 * 
 * @see toolbox.plugin.netmeter.Client
 */
public class Server extends AbstractService
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Server socket which sits and waits for client connections.
     */
    private SocketServer server_;
    
    /**
     * Server port.
     */
    private int port_;
    
    /**
     * Socket server listener. 
     */
    private ISocketServerListener serverListener_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Launches the console base Server. The only argument is the port number.
     * 
     * @param args First arg is the server port number.
     * @throws Exception on error.
     */
    public static void main(String args[]) throws Exception
    {
        int port;
        
        switch (args.length)
        {
            case 1  : port = Integer.parseInt(args[0]); break;
            default : port = 19999; 
        }
        
        Server s = new Server(port);
        s.start();
        
        // Must Ctrl-C to stop the server
        Thread.currentThread().join();
    }
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Server with the default NetmeterPlugin.DEFAULT_PORT.
     */
    public Server()
    {
        this(NetMeterPlugin.DEFAULT_PORT);
    }
    
    
    /**
     * Creates a Server with the given port.
     * 
     * @param port Server port
     */
    public Server(int port)
    {
        setPort(port);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Return the server port number.
     * 
     * @return int
     */
    public int getPort()
    {
        return port_;
    }

    
    /**
     * Sets the server port number.
     * 
     * @param port Port number.
     */
    public void setPort(int port)
    {
        port_ = port;
    }
    
    
    /**
     * Returns the internal socket server.
     * 
     * @return SocketServer
     */
    public SocketServer getSocketServer() 
    {
        return server_;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.AbstractService#initialize(Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        SocketServerConfig config = new SocketServerConfig();
        config.setName("NetMeterServer");
        config.setServerPort(port_);
        
        config.setConnectionHandlerType(
            "toolbox.plugin.netmeter.ServerConnectionHandler");
        
        server_ = new MySocketServer(config);
        
        //
        // Add listener so we can grab references to the created 
        // IConnectionHandlers for cleanup on shutdown.
        //
        
        serverListener_ = new ServerListener();        
        server_.addSocketServerListener(serverListener_);
        
        super.initialize(configuration);
    }
    
    //--------------------------------------------------------------------------
    // MySocketServer
    //--------------------------------------------------------------------------
    
    class MySocketServer extends SocketServer 
    {
        
        public MySocketServer(SocketServerConfig config) 
        {
            super(config);
        }
        
        /**
         * @see toolbox.util.net.SocketServer#getConnectionHandler()
         */
        public IConnectionHandler getConnectionHandler()
        {
            AsyncConnectionHandler async = 
                (AsyncConnectionHandler) super.getConnectionHandler();
            
            ServerConnectionHandler handler = new ServerConnectionHandler();
            async.setConnectionHandler(handler);
            return async;
        }
    }
    
    
    /**
     * @see toolbox.util.service.Service#start()
     */
    public void start() throws ServiceException
    {
        try
        {
            server_.start();
            super.start();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
    }


    /**
     * @see toolbox.util.service.Service#stop()
     */
    public void stop() throws ServiceException
    {
        try
        {
            server_.stop();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        finally
        {
            super.stop();
        }
    }


    /**
     * @see toolbox.util.service.Service#suspend()
     */
    public void suspend() throws ServiceException
    {
        throw new UnsupportedOperationException("Pause not supported");
    }


    /**
     * @see toolbox.util.service.Service#resume()
     */
    public void resume() throws ServiceException
    {
        throw new UnsupportedOperationException("Resume not supported");
    }


    /**
     * @see toolbox.util.service.Service#isRunning()
     */
    public boolean isRunning()
    {
        throw new UnsupportedOperationException("isRunning not supported");
    }


    /**
     * @see toolbox.util.service.Service#isSuspended()
     */
    public boolean isSuspended()
    {
        throw new UnsupportedOperationException("isPaused not supported");
    }
    
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
    }
    
    //--------------------------------------------------------------------------
    // ServerListener 
    //--------------------------------------------------------------------------
    
    /**
     * ServerListener.
     */
    class ServerListener implements ISocketServerListener
    {
        /**
         * @see toolbox.util.net.ISocketServerListener#socketAccepted(
         *      java.net.Socket, toolbox.util.net.IConnection)
         */
        public void socketAccepted(Socket socket, IConnection connection)
        {
        }


        /**
         * @see toolbox.util.net.ISocketServerListener#serverStarted(
         *      toolbox.util.net.SocketServer)
         */
        public void serverStarted(SocketServer server)
        {
        }


        /**
         * @see toolbox.util.net.ISocketServerListener#connectionHandled(
         *      toolbox.util.net.IConnectionHandler)
         */
        public void connectionHandled(IConnectionHandler connectionHandler)
        {
        }
        
        /**
         * @see toolbox.util.net.ISocketServerListener#serverStopped(
         *      toolbox.util.net.SocketServer)
         */
        public void serverStopped(SocketServer server)
        {
        }
    }
}