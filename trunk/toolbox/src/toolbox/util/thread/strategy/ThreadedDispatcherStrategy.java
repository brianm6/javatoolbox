package toolbox.util.thread.strategy;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;
import toolbox.util.thread.ThreadContainer;

/**
 * ThreadedDispatcherStrategy is the abstract class for all threaded 
 * publication strategies. It offers a single method for thread creation. 
 */
public abstract class ThreadedDispatcherStrategy 
    extends AbstractDispatcherStrategy
{
    private int pendingResults_;
    private ThreadContainer container_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new threaded publication strategy.
     */
    ThreadedDispatcherStrategy()
    {
        container_ = new ThreadContainer();
        pendingResults_ = 0;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Publishes the request in an alternate thread and returns a future
     * object that can interrogate the result.
     *
     * @param request Request to publish.
     * @return Future object encapsualting the request result
     */
    public ReturnValue dispatch(IThreadable request)
    {
        ReturnValue result = new ReturnValue();
        serviceRequest(request, result);

        return result;
    }


    /**
     * Publish the request in an alternate thread using the supplied
     * callback to supply status information about the response.
     *
     * @param request Request to publish.
     * @param callback Callback to receive status.
     */
    public void dispatchAsync(IThreadable request,ReturnValue.Listener callback)
    {
        ReturnValue result = new ReturnValue(request, callback);
        serviceRequest(request, result);
    }


    /**
     * Blocks the current thread until all pending requests are complete. 
     */
    public final void join()
    {
        join(0);
    }


    /**
     * Blocks the current thread until all pending requests are complete or
     * the timeout has elapsed.
     *
     * @param  timeout  Timeout value in milliseconds.  If 0, the join will 
     *                  wait indefinitely.
     */
    public synchronized void join(long timeout)
    {
        while (pendingResults_ > 0)
        {
            try
            {
                wait(timeout);

                return;
            }
            catch (InterruptedException e)
            {

                // try again
            }
        }
    }


    //--------------------------------------------------------------------------
    // Abstract Protected
    //--------------------------------------------------------------------------
    
    /**
     * Services the request in an alternate thread and records the result.
     *
     * @param request Request to publish.
     * @param result Holds the request result.
     */
    protected abstract void service(IThreadable request, ReturnValue result);


    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Services the request and manages the set of active requests.
     *
     * @param request Request to publish.
     * @param result Holds the request result
     */
    protected void serviceRequest(IThreadable request, ReturnValue result)
    {
        synchronized (this)
        {
            ++pendingResults_;
        }

        service(request, result);
    }


    /**
     * Indicates the request corresponding to returnValues is processing.
     * This is made protected so only publication strategies or classes in 
     * this package can update the state.
     *
     * @param returnValue Return value to update state for.
     */
    protected void setStarted(ReturnValue returnValue)
    {
        returnValue.setStarted();
    }


    /**
     * Assigns the value contained in the returnValue.  This is made
     * protected so only publication strategies or classes in this package
     * can set the value.
     *
     * @param result Return value to contain the value
     * @param value Value to assign to the return value
     */
    protected void setResult(ReturnValue result, Object value)
    {
        result.setValue(value);

        synchronized (this)
        {
            if (--pendingResults_ == 0)
                notifyAll();
        }
    }


    /**
     * Creates and starts a new thread encapsualting the runnable.
     *
     * @param runnable Runnable to run in the thread.
     * @return Newly created thread.
     */
    protected Thread createThread(Runnable runnable)
    {
        Thread thread = container_.createThread(runnable);
        thread.setDaemon(makeDaemon());
        thread.start();

        return thread;
    }


    /**
     * Creates and starts howMany new threads encapsualting the runnable.
     *
     * @param howMany Number of threads to create.
     * @param runnable Runnable to run in the thread.
     * @return Array of newly created threads.
     */
    protected Thread[] createThreads(int howMany, Runnable runnable)
    {
        Thread[] threads = new Thread[howMany];

        for (int i = 0; i < howMany; ++i)
            threads[i] = createThread(runnable);

        return threads;
    }


    /**
     * Returns true if the threads created by this strategy should be made 
     * daemon threads.
     *
     * @return True if threads should be daemon.
     */
    protected boolean makeDaemon()
    {
        return true;
    }
}