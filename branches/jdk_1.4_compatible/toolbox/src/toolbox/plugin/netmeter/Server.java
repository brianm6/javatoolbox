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
import toolbox.util.service.Initializable;
import toolbox.util.service.ObservableService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceNotifier;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * Server is a non-UI component that is used to collect data throughput
 * statistics when connected to by a Client.
 * 
 * @see toolbox.plugin.netmeter.Client
 */
public class Server implements Startable, Initializable, ObservableService
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Statemachine for the lifecycle states of this server.
     */
    private StateMachine machine_;
    
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
    
    /**
     * Notifier for service related events.
     */
    private ServiceNotifier notifier_;
    
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
     * Creates a Server with the default NetmeterPlugin.DEFAULT_PORT of 9999.
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
        
        // Create a state machine that adheres to the natures of this server
        machine_ = ServiceUtil.createStateMachine(this);
        notifier_ = new ServiceNotifier(this);
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
    // Initializable Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.INITIALIZE);
        
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
        
        machine_.transition(ServiceTransition.INITIALIZE);
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        try
        {
            server_.start();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        
        machine_.transition(ServiceTransition.START);
    }


    /*
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.STOP);
        
        try
        {
            server_.stop();
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        
        machine_.transition(ServiceTransition.STOP);
    }


    /*
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // ObservableService Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.ObservableService#addServiceListener(toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        notifier_.addServiceListener(listener);
    }


    /*
     * @see toolbox.util.service.ObservableService#removeServiceListener(toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener)
    {
        notifier_.removeServiceListener(listener);
    }
    
    //--------------------------------------------------------------------------
    // MySocketServer
    //--------------------------------------------------------------------------
    
    /**
     * MySocketServer is responsible for handling incoming connections from
     * clients. 
     */
    class MySocketServer extends SocketServer 
    {
        public MySocketServer(SocketServerConfig config) 
        {
            super(config);
        }
        
        
        /*
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
    
    //--------------------------------------------------------------------------
    // ServerListener 
    //--------------------------------------------------------------------------
    
    /**
     * ServerListener.
     */
    class ServerListener implements ISocketServerListener
    {
        /*
         * @see toolbox.util.net.ISocketServerListener#socketAccepted(java.net.Socket, toolbox.util.net.IConnection)
         */
        public void socketAccepted(Socket socket, IConnection connection)
        {
        }


        /*
         * @see toolbox.util.net.ISocketServerListener#serverStarted(toolbox.util.net.SocketServer)
         */
        public void serverStarted(SocketServer server)
        {
        }


        /*
         * @see toolbox.util.net.ISocketServerListener#connectionHandled(toolbox.util.net.IConnectionHandler)
         */
        public void connectionHandled(IConnectionHandler connectionHandler)
        {
        }
        
        
        /*
         * @see toolbox.util.net.ISocketServerListener#serverStopped(toolbox.util.net.SocketServer)
         */
        public void serverStopped(SocketServer server)
        {
        }
    }
}