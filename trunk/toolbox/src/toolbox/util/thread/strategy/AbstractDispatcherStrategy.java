package toolbox.util.thread.strategy;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;

/**
 * AbstractDispatcherStrategy encapsulates a threading policy for request 
 * processing. Implementations of this class can choose how a request is 
 * processed, single threaded, thread-per-request, thread-pool, etc.  The 
 * default implementation processes the request in the current thread.
 */
public abstract class AbstractDispatcherStrategy
{
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Publish the request by processing it in the current thread.
     *
     * @param request Request to publish.
     * @return ReturnValue encapsualting the request result.
     */
    public ReturnValue dispatch(IThreadable request)
    {
        return new ReturnValue(process(request));
    }


    /**
     * Publish the request by processing it in the current thread and
     * block until the request is completed.
     *
     * @param request Request to publish.
     * @return Request result.
     */
    public final Object dispatchAndWait(IThreadable request)
    {
        return dispatch(request).getValue();
    }


    /**
     * Processes the request in the current thread.
     *
     * @param request Request to process
     * @return Result of the request or null if no result. 
     */
    public Object process(IThreadable request)
    {
        return request.run();
    }


    /**
     * Performs any cleanup activities.
     */
    public void shutdown()
    {
    }
}