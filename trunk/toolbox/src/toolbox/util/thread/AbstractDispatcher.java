package toolbox.util.thread;

import toolbox.util.thread.strategy.AbstractDispatcherStrategy;

/**
 * AbstractDispatcher encapsulates the publishing mechanism for asynchronous 
 * requests. The default implementation simply passes the request to the 
 * specified threading strategy.
 */
public abstract class AbstractDispatcher
{
    /**
     * Flag for the current run state of the dispatcher.
     */
    private boolean running_;
    
    /**
     * Dispatching strategy.
     */
    private AbstractDispatcherStrategy strategy_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a new request publisher using the specified strategy.
     *
     * @param strategy Strategy that encapsualtes request delivery and 
     *        processing.
     */
    public AbstractDispatcher(AbstractDispatcherStrategy strategy)
    {
        running_ = true;
        strategy_ = strategy;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the associated strategy.
     *
     * @return Associated strategy.
     */
    public AbstractDispatcherStrategy strategy()
    {
        return strategy_;
    }


    /**
     * Passes the request to the associated strategy for processing.
     *
     * @param request Request to publish.
     * @return ReturnValue encapsualting the request result.
     */
    public ReturnValue dispatch(IThreadable request)
    {
        if (!running_)
            throw new IllegalStateException();

        return strategy_.dispatch(request);
    }


    /**
     * Passes the request to the associated strategy for processing and waits 
     * for result.
     *
     * @param request Request to publish.
     * @return Request result.
     */
    public Object dispatchAndWait(IThreadable request)
    {
        if (!running_)
            throw new IllegalStateException();

        return strategy_.dispatchAndWait(request);
    }


    /**
     * Terminates all publishing activities. 
     */
    public final synchronized void shutdown()
    {
        if (running_)
        {
            strategy_.shutdown();
            running_ = false;
        }
    }
    
    
    /**
     * Returns the running.
     * 
     * @return boolean
     */
    public boolean isRunning()
    {
        return running_;
    }


    /**
     * Returns the strategy.
     * 
     * @return AbstractDispatcherStrategy
     */
    public AbstractDispatcherStrategy getStrategy()
    {
        return strategy_;
    }
}