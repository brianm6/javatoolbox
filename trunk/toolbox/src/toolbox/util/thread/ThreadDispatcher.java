package toolbox.util.thread;

/**
 * ThreadDispatcher.java
 *
 * This class specializes the default AbstractDispatcher to service requests 
 * using one or more threading strategies and to enable the joining of
 * the request processing.
 */
public class ThreadDispatcher extends AbstractDispatcher
{

    /**
    * Constructs a new request publisher using the threading strategy.
    *
    * @param    strategy         threading strategy that service requests.
    */
    public ThreadDispatcher(ThreadedDispatcherStrategy strategy)
    {
        super(strategy);
    }


    /**
    * Publish the request in an alternate thread using the supplied
    * callback to supply status information about the response.
    *
    * @param    request        the request to publish.
    * @param    callback    the callback to receive status.
    * @exception IllegalStateException if a join is currently active on this
    *            publication strategy.
    */
    public void dispatchAsync(IThreadable request, ReturnValue.Listener callback)
    {
        if (!running_)
            throw new IllegalStateException();

        ((ThreadedDispatcherStrategy)strategy_).dispatchAsync(request, 
                                                              callback);
    }


    /**
    * Blocks the current thread until all pending requests are complete. 
    */
    public void join()
    {
        ((ThreadedDispatcherStrategy)strategy_).join();
    }


    /**
    * Blocks the current thread until all pending requests are complete or
    * the timeout has elapsed.
    *
    * @param    timeout         the timeout value in milliseconds.  If 0,
    *                            the join will wait indefinitely.
    */
    public void join(long timeout)
    {
        ((ThreadedDispatcherStrategy)strategy_).join(timeout);
    }
}