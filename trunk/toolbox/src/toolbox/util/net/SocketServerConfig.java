package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import toolbox.util.ResourceCloser;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;

/**
 * Encapsulates configuration properties for a Socket Server
 */
public class SocketServerConfig
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(SocketServerConfig.class);

    /** 
     * Property for server socket port number
     */
    public static final String PROP_SERVER_PORT = 
        "socketserver.serverport";

    /** 
     * Property for max number of threads in pool 
     */
    public static final String PROP_ACTIVE_CONNECTIONS = 
        "socketserver.activeconnections";

    /** 
     * Property for waiting queue size for socket 
     */
    public static final String PROP_SOCKET_QUEUE_SIZE = 
        "socketserver.socketqueuesize";

    /** 
     * Property for waiting queue size for the handler/thread pool 
     */
    public static final String PROP_HANDLER_QUEUE_SIZE = 
        "socketserver.handlerqueuesize";

    /** 
     * Property for server socket timeout 
     */
    public static final String PROP_SOCKET_TIMEOUT = 
        "socketserver.sockettimeout";

    /**
     * Connection handler for the socket server. 
     * This must specify a class name that implements 
     * the IConnectionHandler interface. 
     */
    public static final String PROP_CONNECTION_HANDLER = 
        "socketserver.connectionhandler";

    /**
     * Property for the name of the socket server
     */
    public static final String PROP_SERVER_NAME = 
        "socketserver.name";

    /**
     * Optional name of socket server for identification purposes
     */
    private String name_;
    
    /** 
     * Server socket port number
     */
    private int serverPort_;

    /** 
     * Max active connections aka size of thread pool 
     */
    private int activeConnections_;

    /** 
     * Number of handlers that can be queued up by the thread pool 
     */
    private int handlerQueueSize_;

    /** 
     * Number of sockets that can be queued up by OS 
     */
    private int socketQueueSize_;
 
    /** 
     * Socket timeout in millis 
     */
    private int socketTimeout_;

    /**
     * Connection handler type for this server config 
     */
    private String connectionHandlerType_;

    /** 
     * Default properties 
     */
    private static Properties defaults_;

    static
    {
        // Set default properties
        defaults_ = new Properties();
        defaults_.put(PROP_SERVER_NAME, "");
        defaults_.put(PROP_ACTIVE_CONNECTIONS, "5");
        defaults_.put(PROP_SERVER_PORT, "0");
        defaults_.put(PROP_HANDLER_QUEUE_SIZE, "10");
        defaults_.put(PROP_SOCKET_QUEUE_SIZE, "20");
        defaults_.put(PROP_SOCKET_TIMEOUT, "30000");
        defaults_.put(PROP_CONNECTION_HANDLER, 
                     "toolbox.util.net.test.EchoConnectionHandler");
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SocketServerConfig with default configuration
     */
    public SocketServerConfig()
    {
        this(defaults_);
    }

    /**
     * Creates a socket server configuration with the given properties
     * 
     * @param    props    Properties
     */
    public SocketServerConfig(Properties props)
    {
        load(props);
    }

    /**
     * Creates a socket server configuration with the 
     * properties from the given file
     * 
     * @param   file    Properties file to read configuration from
     * @throws  IOException on I/O error
     */
    public SocketServerConfig(String file) throws IOException
    {
        load(file);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Loads configuration properties from a file on the classpath
     * 
     * @param    file    Properties file on the classpath
     * @throws   IOException on I/O error
     */
    public void load(String file) throws IOException
    {
        // Has to start with a slash to search from root of classpath
        //if (!file.startsWith("/"))
        //    file = "/" + file;

        InputStream is = null;

        try
        {
            //is = ResourceUtil.getClassResource(file);
            is = ResourceUtil.getResource(file);
            
            Properties props = new Properties();
            props.load(is);
            load(props);
        }
        finally
        {
            ResourceCloser.close(is);
        }
    }

    /**
     * Loads configuration properties from an existing set of properties 
     *
     * @param    props    Properties
     */
    public void load(Properties props)
    {
        // Load from props. use defaults if any properties are missing
        setServerPort(
            Integer.parseInt(props.getProperty(PROP_SERVER_PORT, 
            defaults_.getProperty(PROP_SERVER_PORT))));
            
        setActiveConnections(
            Integer.parseInt(props.getProperty(PROP_ACTIVE_CONNECTIONS, 
            defaults_.getProperty(PROP_ACTIVE_CONNECTIONS))));
            
        setSocketQueueSize(
            Integer.parseInt(props.getProperty(PROP_SOCKET_QUEUE_SIZE, 
            defaults_.getProperty(PROP_SOCKET_QUEUE_SIZE))));
            
        setHandlerQueueSize(
            Integer.parseInt(props.getProperty(PROP_HANDLER_QUEUE_SIZE, 
            defaults_.getProperty(PROP_HANDLER_QUEUE_SIZE))));
            
        setSocketTimeout(
            Integer.parseInt(props.getProperty(PROP_SOCKET_TIMEOUT, 
            defaults_.getProperty(PROP_SOCKET_TIMEOUT))));
            
        setConnectionHandlerType(
            props.getProperty(PROP_CONNECTION_HANDLER, 
            defaults_.getProperty(PROP_CONNECTION_HANDLER)));
            
        setName(
            props.getProperty(PROP_SERVER_NAME,
            defaults_.getProperty(PROP_SERVER_NAME)));
    }

    /**
     * Accessor for server socket port
     * 
     * @return    int
     */
    public int getServerPort()
    {
        return serverPort_;
    }

    /**
     * Mutator for server socket port
     * 
     * @param    port    Server socket port
     */
    public void setServerPort(int port)
    {
        serverPort_ = port;
    }

    /**
     * Accessor for the max number of active connections (threads)
     * 
     * @return    int
     */
    public int getActiveConnections()
    {
        return activeConnections_;
    }

    /**
     * Mutator for the max number of active connections
     * 
     * @param    activeConnections    Max active connections
     */
    public void setActiveConnections(int activeConnections)
    {
        activeConnections_ = activeConnections;
    }

    /**
     * Accessor for the max number of connections in the socket waiting queue
     * 
     * @return    int
     */
    public int getSocketQueueSize()
    {
        return socketQueueSize_;
    }

    /**
     * Mutator for the max number of connections in the socket waiting queue
     * 
     * @param    socketQueueSize        Max number of queued sockets
     */
    public void setSocketQueueSize(int socketQueueSize)
    {
        socketQueueSize_ = socketQueueSize;
    }

    /**
     * Accessor for the max number of connections in the handler waiting queue
     * 
     * @return    int
     */
    public int getHandlerQueueSize()
    {
        return handlerQueueSize_;
    }

    /**
     * Mutator for the max number of connections in the handler waiting queue
     * 
     * @param    handlerQueueSize    Max number of queued handlers
     */
    public void setHandlerQueueSize(int handlerQueueSize)
    {
        handlerQueueSize_ = handlerQueueSize;
    }

    /**
     * Accessor for the server socket timeout
     * 
     * @return    int
     */
    public int getSocketTimeout()
    {
        return socketTimeout_;
    }

    /**
     * Mutator for the server socket timeout
     * 
     * @param    socketTimeout    Server socket timeout
     */
    public void setSocketTimeout(int socketTimeout)
    {
        socketTimeout_ = socketTimeout;
    }

    /**
     * Returns IConnectionHandler class
     * 
     * @return    IConnectionHandler
     */
    public IConnectionHandler getConnectionHandler()
    {
        String clazzName = getConnectionHandlerType();

        if (StringUtil.isNullOrEmpty(clazzName))
        {
            throw new IllegalStateException("Connection handler not set.");
        }

        Class              clazz   = null;
        IConnectionHandler handler = null;

        try
        {
            clazz = Class.forName(clazzName);
            handler = (IConnectionHandler)clazz.newInstance();
        }
        catch (ClassNotFoundException cnfe)
        {
            logger_.error("Could not find IConnectionHandler subclass: " + 
                         clazzName, cnfe);
        }
        catch (IllegalAccessException iae)
        {
            logger_.error("While instantiating " + clazz, iae);
        }
        catch (InstantiationException ie)
        {
            logger_.error("While instantiating " + clazz, ie);
        }

        return handler;
    }

    /** 
     * Returns type of connection handler 
     * 
     * @return    String
     */
    public String getConnectionHandlerType()
    {
        return connectionHandlerType_;
    }

    /**
     * Sets connection handler type.
     * 
     * @param  type  String containing FQN of class implementing 
     *               IConnectionHandler
     */
    public void setConnectionHandlerType(String type)
    {
        connectionHandlerType_ = type;
    }

    /**
     * Returns the name.
     * 
     * @return String
     */
    public String getName()
    {
        return name_;
    }

    /**
     * Sets the name.
     * 
     * @param name The name to set
     */
    public void setName(String name)
    {
        name_ = name;
    }

    //--------------------------------------------------------------------------
    //  Overridden from java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * @return Configuration as a string
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }
}
