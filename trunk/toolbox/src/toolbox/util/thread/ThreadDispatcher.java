package toolbox.util.thread;

import toolbox.util.thread.strategy.ThreadedDispatcherStrategy;

/**
 * ThreadDispatcher specializes the default AbstractDispatcher to service 
 * requests using one or more threading strategies and to enable the joining of
 * the request processing.
 */
public class ThreadDispatcher extends AbstractDispatcher
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a new request publisher using the threading strategy.
     *
     * @param strategy Threading strategy that service requests.
     */
    public ThreadDispatcher(ThreadedDispatcherStrategy strategy)
    {
        super(strategy);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Publish the request in an alternate thread using the supplied
     * callback to supply status information about the response.
     *
     * @param request Request to publish.
     * @param callback Callback to receive status.
     * @throws IllegalStateException if a join is currently active on this
     *         publication strategy.
     */
    public void dispatchAsync(
        IThreadable request,
        ReturnValue.Listener callback)
        throws IllegalStateException
    {
        if (!isRunning())
            throw new IllegalStateException();

        ((ThreadedDispatcherStrategy) getStrategy()).dispatchAsync(
            request,
            callback);
    }


    /**
     * Blocks the current thread until all pending requests are complete. 
     */
    public void join()
    {
        ((ThreadedDispatcherStrategy) getStrategy()).join();
    }


    /**
     * Blocks the current thread until all pending requests are complete or
     * the timeout has elapsed.
     *
     * @param timeout Timeout value in milliseconds.  If 0, the join will 
     *        wait indefinitely.
     */
    public void join(long timeout)
    {
        ((ThreadedDispatcherStrategy) getStrategy()).join(timeout);
    }
}