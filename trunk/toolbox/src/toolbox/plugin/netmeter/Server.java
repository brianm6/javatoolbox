package toolbox.plugin.netmeter;

import java.io.IOException;

import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;
import toolbox.util.service.*;

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
        finally
        {
            super.start();
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
        finally
        {
            super.stop();
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