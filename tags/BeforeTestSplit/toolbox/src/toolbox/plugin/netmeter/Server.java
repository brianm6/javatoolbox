package toolbox.plugin.netmeter;

import java.io.IOException;
import java.net.Socket;

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
        port_ = port;
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
        
    //--------------------------------------------------------------------------
    // Protected  
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the server.
     * 
     * @throws IOException on I/O error.
     */
    protected void init() throws IOException
    {
        SocketServerConfig config = new SocketServerConfig();
        config.setName("NetMeterServer");
        config.setServerPort(port_);
        
        config.setConnectionHandlerType(
            "toolbox.plugin.netmeter.ServerConnectionHandler");
        
        server_ = new SocketServer(config);
        
        //
        // Add listener so we can grab references to the created 
        // IConnectionHandlers for cleanup on shutdown.
        //
        serverListener_ = new ServerListener();        
        server_.addSocketServerListener(serverListener_);
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Service#start()
     */
    public void start() throws ServiceException
    {
        try
        {
            if (server_ == null)
                init();
                
            server_.start();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        finally
        {
            super.start();
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
     * @see toolbox.util.service.Service#pause()
     */
    public void pause() throws ServiceException
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
     * @see toolbox.util.service.Service#isPaused()
     */
    public boolean isPaused()
    {
        throw new UnsupportedOperationException("isPaused not supported");
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