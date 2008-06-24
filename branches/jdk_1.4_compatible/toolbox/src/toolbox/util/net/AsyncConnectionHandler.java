package toolbox.util.net;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ThreadDispatcher;

/**
 * AsyncConnectionHandler decorates a connection handler by providing 
 * asynchronous dispatching (when handle() is called) on a separate pooled 
 * thread. The call to handle() returns immediately and is non-blocking call.
 */
public class AsyncConnectionHandler implements IConnectionHandler, IThreadable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Connection to handle. 
     */
    private IConnection conn_;

    /** 
     * Connection handler delegate. 
     */
    private IConnectionHandler handler_;

    /** 
     * Dispatcher responsible for pooling the connection handlers.
     */
    private ThreadDispatcher dispatcher_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AsyncConnectionHandler.
     * 
     * @param handler Handler to wrap with async behavior.
     * @param dispatcher Dispatcher to use for thread acquisition/dispatching.
     */
    public AsyncConnectionHandler(
        IConnectionHandler handler, 
        ThreadDispatcher dispatcher)
    {
        setConnectionHandler(handler);
        setDispatcher(dispatcher);
    }

    //--------------------------------------------------------------------------
    // IConnectionHandler Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.net.IConnectionHandler#handle(
     *      toolbox.util.net.IConnection)
     */
    public Object handle(IConnection conn)
    {
        setConnection(conn);
        return getDispatcher().dispatch(this);
    }

    //--------------------------------------------------------------------------
    // IThreadable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.thread.IThreadable#run()
     */
    public Object run()
    {
        return getConnectionHandler().handle(getConnection());
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * Mutator for the connection handler.
     * 
     * @param handler IConnectionHandler.
     */
    public void setConnectionHandler(IConnectionHandler handler)
    {
        handler_ = handler;
    }


    /**
     * Mutator for the connection.
     * 
     * @param connection IConnection.
     */
    public void setConnection(IConnection connection)
    {
        conn_ = connection;
    }


    /**
     * Accessor for the connection.
     * 
     * @return IConnection.
     */
    public IConnection getConnection()
    {
        return conn_;
    }


    /**
     * Accessor for the connection handler. 
     * 
     * @return IConnectionHandler.
     */
    public IConnectionHandler getConnectionHandler()
    {
        return handler_;
    }


    /**
     * Mutator for the dispatcher.
     * 
     * @param dispatcher Dispatcher.
     */
    public void setDispatcher(ThreadDispatcher dispatcher)
    {
        dispatcher_ = dispatcher;
    }


    /**
     * Accessor for the dispatcher.
     * 
     * @return Dispatcher.
     */
    public ThreadDispatcher getDispatcher()
    {
        return dispatcher_;
    }
}