package toolbox.util.thread;

/**
 * AbstractDispatcherStrategy.java
 *
 * This class encapsulates the threading policy for request processing.
 * Implementations of this class can choose how a request is processed,
 * single threaded, thread-per-request, thread-pool, etc.  The default
 * implementation processes the request in the current thread.
 */
public abstract class AbstractDispatcherStrategy
{

    /**
    * Publish the request by processing it in the current thread.
    *
    * @return   the ReturnValue encapsualting the request result.
    * @param    request        the request to publish.
    */
    public ReturnValue dispatch(IThreadable request)
    {
        return new ReturnValue(process(request));
    }


    /**
    * Publish the request by processing it in the current thread and
    * block until the request is completed.
    *
    * @return   the request result.
    * @param    request        the request to publish.
    */
    public final Object dispatchAndWait(IThreadable request)
    {
        return dispatch(request).getValue();
    }


    /**
    * Processes the request in the current thread.
    *
    * @return   the result of the request or null if no result. 
    * @param    request        the request to process.
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