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
    private SocketServer server_;
    private int port_ = 19999;
    
    //--------------------------------------------------------------------------
    // Constrcutors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Server.
     */
    public Server()
    {
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
        config.setName("Server");
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