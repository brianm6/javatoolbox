package toolbox.plugin.netmeter;

import java.io.IOException;

import javax.swing.JPanel;

import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;

/**
 * NetMeter Server.
 */
public class Server extends JPanel implements Service
{
    /**
     * Default server port if one is not specified.
     */
    public static final int DEFAULT_PORT = 19999;

    /**
     * Server socket.
     */
    private SocketServer server_;
    
    /**
     * Server port.
     */
    private int port_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
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
     * Creates a Server.
     */
    public Server()
    {
        this(DEFAULT_PORT);
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
    // Protected  
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the server.
     * 
     * @throws IOException on I/O error
     */
    protected void init() throws IOException
    {
        SocketServerConfig config = new SocketServerConfig();
        config.setName("NetMeterServer");
        config.setServerPort(port_);
        
        config.setConnectionHandlerType(
            "toolbox.plugin.netmeter.ServerConnectionHandler");
            
        server_ = new SocketServer(config);
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.plugin.netmeter.Service#start()
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
    }


    /**
     * @see toolbox.plugin.netmeter.Service#stop()
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
    }


    /**
     * @see toolbox.plugin.netmeter.Service#pause()
     */
    public void pause() throws ServiceException
    {
        throw new UnsupportedOperationException("Pause not supported");
    }


    /**
     * @see toolbox.plugin.netmeter.Service#resume()
     */
    public void resume() throws ServiceException
    {
        throw new UnsupportedOperationException("Resume not supported");
    }


    /**
     * @see toolbox.plugin.netmeter.Service#isRunning()
     */
    public boolean isRunning()
    {
        throw new UnsupportedOperationException("isRunning not supported");
    }


    /**
     * @see toolbox.plugin.netmeter.Service#isPaused()
     */
    public boolean isPaused()
    {
        throw new UnsupportedOperationException("isPaused not supported");
    }
}