package toolbox.util.thread.strategy;

import java.util.ArrayList;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;
import toolbox.util.thread.concurrent.BoundedBufferAdapter;
import toolbox.util.thread.concurrent.IBoundedBuffer;


/**
 * ThreadPoolStrategy implements a thread-pool strategy that puts requests on a
 * queue for one or more worker threads to extract.  Since all the threads
 * are created up front, the thread creation penalty is incurred on startup
 * and is thus better suited for bursty situations.  However, since a bounded
 * number of threads is created, once all threads are occupied,or the queue is
 * full,  the sender will block until a thread is available to process it, or
 * the queue becomes unsaturated.
 */
public class ThreadPoolStrategy extends ThreadedDispatcherStrategy
{
    /**
     * Default pool size
     */
    public static final int DEFAULT_POOL_SIZE = 10;
    
    /**
     * Default queue size
     */
    public static final int DEFAULT_QUEUE_SIZE = 100;
    
    private int poolSize_;
    private Runnable runnable_;
    private IBoundedBuffer requestQueue_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a default thread pool.
     */
    public ThreadPoolStrategy()
    {
        this(DEFAULT_POOL_SIZE, DEFAULT_QUEUE_SIZE);
    }


    /**
     * Creates a thread pool consisting of poolSize threads and a queue of
     * queueSize.
     * 
     * @param poolSize Number of threads in the pool.
     * @param queueSize Maximum number of buffered requests.
     */
    public ThreadPoolStrategy(int poolSize, int queueSize)
    {
        poolSize_ = poolSize;
        runnable_ = new ThreadPoolRunnable();
        requestQueue_ = new BoundedBufferAdapter(new ArrayList(), queueSize);
        createThreads(poolSize_, runnable_);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Services the request by putting it on the request queue. If the queue is
     * full, the calling thread is blocked until a slot becomes available.
     * 
     * @param request Request to publish.
     * @param result Holds the request result.
     */
    public void service(IThreadable request, ReturnValue result)
    {
        requestQueue_.put(new Task(request, result));
    }


    /** 
     * Publish a null request for each thread in the pool to signal shutdown.
     */
    public void shutdown()
    {
        for (int i = 0; i < poolSize_; ++i)
            requestQueue_.put(null);
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
        
    /**
     * A Task encapsulates a request and its result.
     */    
    static class Task
    {
        private IThreadable request_;
        private ReturnValue result_;

        /**
         * Creates a Task.
         * 
         * @param request Request.
         * @param result Return value.
         */
        public Task(IThreadable request, ReturnValue result)
        {
            request_ = request;
            result_ = result;
        }
    }

    
    /**
     * Strategy specific runnable for thread-pool strategy.
     */
    class ThreadPoolRunnable implements Runnable
    {
        /**
         * Process the next available task on the queue or block until
         * one becomes available.  A null task instructs this strategy
         * to stop reading further tasks.
         */
        public void run()
        {
            Task task = null;

            while ((task = (Task) requestQueue_.take()) != null)
            {
                try
                {
                    setStarted(task.result_);
                    setResult(task.result_, process(task.request_));
                }
                catch (Exception e)
                {
                    setResult(task.result_, e);
                }
            }
        }
    }
}