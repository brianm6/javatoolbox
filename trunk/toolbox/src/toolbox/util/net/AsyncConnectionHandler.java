package toolbox.util.net;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ThreadDispatcher;

/**
 * Decorates a connection handler to provide asynchronous dispatching 
 * (when handle() is called) on a separate pooled thread. handle()
 * returns immediately and is not a blocking call.
 */
public class AsyncConnectionHandler implements IConnectionHandler,  IThreadable
{
    /** 
     * Connection to handle 
     */
    private IConnection conn_;

    /** 
     * Connection handler to asynchronously run in a separate thread of 
     * execution
     */
    private IConnectionHandler handler_;

    /** 
     * Dispatcher responsible for thread pool behavior 
     */
    private ThreadDispatcher dispatcher_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates AsyncConnectionHandler
     * 
     * @param newHandler    Handler to wrap with async behavior
     * @param newDispatcher Dispatcher to use for thread acquisition/dispatching
     */
    public AsyncConnectionHandler(IConnectionHandler newHandler, 
                                  ThreadDispatcher newDispatcher)
    {
        setConnectionHandler(newHandler);
        setDispatcher(newDispatcher);
    }

    //--------------------------------------------------------------------------
    //  Interface IConnectionHandler
    //--------------------------------------------------------------------------
    
    /**
     * Initiates handling of the connection on a new thread.
     * 
     * @param    conn    Connection to handle
     * @return   Object
     */
    public Object handle(IConnection conn)
    {
        setConnection(conn);

        return getDispatcher().dispatch(this);
    }

    //--------------------------------------------------------------------------
    //  Interface IThreadable
    //--------------------------------------------------------------------------
    
    /**
     * Implemented as part of the Request interface.
     * Processes request once on a separate thread
     * 
     * @return    Object
     */
    public Object run()
    {
        return getConnectionHandler().handle(getConnection());
    }

    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * Mutator for the connection handler
     * 
     * @param    newHandler        IConnectionHandler
     */
    public void setConnectionHandler(IConnectionHandler newHandler)
    {
        handler_ = newHandler;
    }


    /**
     * Mutator for the connection
     * 
     * @param    newConn    IConnection
     */
    public void setConnection(IConnection newConn)
    {
        conn_ = newConn;
    }


    /**
     * Accessor for the connection
     * 
     * @return    IConnection
     */
    public IConnection getConnection()
    {
        return conn_;
    }


    /**
     * Accessor for the connection handler 
     * 
     * @return    IConnectionHandler
     */
    public IConnectionHandler getConnectionHandler()
    {
        return handler_;
    }


    /**
     * Mutator for the dispatcher
     * 
     * @param    newDispatcher    Dispatcher
     */
    public void setDispatcher(ThreadDispatcher newDispatcher)
    {
        dispatcher_ = newDispatcher;
    }


    /**
     * Accessor for the dispatcher
     * 
     * @return    Dispatcher
     */
    public ThreadDispatcher getDispatcher()
    {
        return dispatcher_;
    }
}
