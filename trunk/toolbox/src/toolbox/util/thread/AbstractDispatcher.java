package toolbox.util.thread;

import toolbox.util.thread.strategy.AbstractDispatcherStrategy;

/**
 * AbstractDispatcher.java
 *
 * This class encapsulates the publishing mechanism for asynchronous requests.
 * The default implementations simply passes the request to the specified
 * threading strategy.
 */
public abstract class AbstractDispatcher
{
    protected boolean running_;
    protected AbstractDispatcherStrategy strategy_;


    /**
     * Constructs a new request publisher using the specified strategy.
     *
     * @param    strategy  Strategy that encapsualtes request
     *                     delivery and processing.
     */
    public AbstractDispatcher(AbstractDispatcherStrategy strategy)
    {
        running_ = true;
        strategy_ = strategy;
    }


    /**
     * Returns the associated strategy.
     *
     * @return    the associated strategy.
     */
    public AbstractDispatcherStrategy strategy()
    {
        return strategy_;
    }


    /**
     * Passes the request to the associated strategy for processing.
     *
     * @param    request        request to publish.
     * @return   the ReturnValue encapsualting the request result.
     */
    public ReturnValue dispatch(IThreadable request)
    {
        if (!running_)
            throw new IllegalStateException();

        return strategy_.dispatch(request);
    }


    /**
     * Passes the request to the associated strategy for processing and
     * waits for result.
     *
     * @param    request  Request to publish.
     * @return   Request result.
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
}