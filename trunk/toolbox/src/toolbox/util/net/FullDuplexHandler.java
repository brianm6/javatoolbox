package toolbox.util.net;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

/**
 * Abstract base class for POS connection handlers. Provides central  mgmt of
 * queue/connection and also recovery/shutdown of the handler
 */
public abstract class FullDuplexHandler implements IConnectionHandler
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(FullDuplexHandler.class);

    /** 
     * Connection to be handled
     */
    private IConnection conn_;

    /**
     * Thread of execution for handling the input stream
     */
    private Thread inputStreamThread_;
    
    /**
     * Thread of execution for handling the output stream
     */
    private Thread outputStreamThread_;

    /**
     * Flag that is set if stop() is pending. Error handler checks this flag and
     * error messages off easy if the stop is pending
     */
    private boolean pendingStop_ = false;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public FullDuplexHandler()
    {
    }

    //--------------------------------------------------------------------------
    //  IConnectionHandler interface
    //--------------------------------------------------------------------------

    /**
     * Common handler for POS message based connections. Functionality includes
     * maintaining a connection if it shoud drop of become corrupt, 
     * handling error and exceptions in a uniform way, and making
     * it straightforward for the handler subclasses to implement
     * their intended functionality without duplication of behavior.
     * 
     * @param   conn   Connection to read/write messages to/from
     * @return  Null
     */
    public final Object handle(IConnection conn) 
    {
        conn_ = conn;
        String method = "[handle] ";

        logger_.info(method + "handle called");
        
        // Startup input/output stream handlers on separate threads
        inputStreamThread_  = ThreadUtil.run(this, "handleInput", null);
        outputStreamThread_ = ThreadUtil.run(this, "handleOutput", null);
        
        return null;
    }

    //--------------------------------------------------------------------------
    //  Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * Handlees the inputstream
     * 
     * @param  is  Inputstream to handle
     * @throws Exception on error
     */
    public abstract void handleInput() throws Exception;
    
        
    /**
     * Handles the output stream
     * 
     * @param  os  OutputStream to handle
     * @throws Exception on error
     */
    public abstract void handleOutput() throws Exception;


    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Mutator for the connection
     * 
     * @param newConnection   The connection
     */
    public void setConnection(IConnection newConnection)
    {
        conn_ = newConnection;
    }


    /**
     * Accessor for the connection
     * 
     * @return IConnection
     */
    public IConnection getConnection()
    {
        return conn_;
    }
}