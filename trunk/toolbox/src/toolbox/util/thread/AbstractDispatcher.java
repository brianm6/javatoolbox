package toolbox.util.thread;

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
    * @param    strategy         strategy that encapsualtes request
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
    * @return   the ReturnValue encapsualting the request result.
    * @param    request        request to publish.
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
    * @return   the request result.
    * @param    request         request to publish.
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