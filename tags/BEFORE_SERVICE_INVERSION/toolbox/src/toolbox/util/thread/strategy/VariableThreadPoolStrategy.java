package toolbox.util.thread.strategy;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;

/**
 * VariableThreadPoolStrategy implements a variable-thread-strategy that is a 
 * combination of thread-per-request at low request frequencies and a 
 * thread-pool strategy at high request frequencies.  If no threads are created 
 * up front, so the initial requests will incur thread creation penalities.  
 * However, after a thread has finished servicing a request, it will make an 
 * effort to service any pending requests before it terminates.  Thus, the same 
 * thread can be reused.  This reduces the number of threads to create during 
 * bursts of requests.
 */
public class VariableThreadPoolStrategy extends ThreadedDispatcherStrategy
{
    private static final Logger logger_ = 
        Logger.getLogger(VariableThreadPoolStrategy.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Default initial size.
     */
    public static final int DEFAULT_INIT_SIZE = 0;
    
    /** 
     * Default grow size. 
     */
    public static final int DEFAULT_GROW_SIZE = 5;
    
    /** 
     * Default pool size. 
     */
    public static final int DEFAULT_POOL_SIZE = 100;
    
    /** 
     * Default queue size. 
     */
    public static final int DEFAULT_QUEUE_SIZE = 100;
    
    /** 
     * Default timeout in millis. 
     */
    public static final int DEFAULT_TIMEOUT = 5000;

    /**
     * Static task that is put on the request queue to signify a shutdown.
     */
    public static final VariableThreadPoolStrategy.Task SHUTDOWN_TASK = 
        new VariableThreadPoolStrategy.Task();
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Initial size of the thread pool.
     */
    private int initSize_;
    
    /**
     * Increment by which to grow.
     */
    private int growSize_;
    
    /**
     * Thread pool size.
     */
    private int poolSize_;
    
    /**
     * Pending size of the pool.
     */
    private int pendingSize_;
    
    /**
     * Current size of the pool.
     */
    private int currentSize_;
    
    /**
     * Busy size fo the.
     */
    private int busySize_;
    
    /**
     * Timeout for requests.
     */
    private int timeout_;
    
    /**
     * Our runnable.
     */
    private Runnable runnable_;
    
    /**
     * Request queue.
     */
    private BoundedBuffer requestQueue_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a default variable thread pool.
     */
    public VariableThreadPoolStrategy()
    {
        this(DEFAULT_INIT_SIZE, 
            DEFAULT_GROW_SIZE, 
            DEFAULT_POOL_SIZE, 
            DEFAULT_QUEUE_SIZE, 
            DEFAULT_TIMEOUT);
    }


    /**
     * Creates a default variable thread pool consisting of initSize threads.
     * 
     * @param initSize Initial number of threads.
     */
    public VariableThreadPoolStrategy(int initSize)
    {
        this(initSize, 
            DEFAULT_GROW_SIZE, 
            DEFAULT_POOL_SIZE, 
            DEFAULT_QUEUE_SIZE, 
            DEFAULT_TIMEOUT);
    }


    /**
     * Creates a variable thread pool of capacity poolSize, increment growSize,
     * queueSize and thread timeout period.
     * 
     * @param initSize Number of threads initially created.
     * @param growSize Number of threads per increment.
     * @param poolSize Maximum number threads createable.
     * @param queueSize Maximum number of buffered requests.
     * @param timeout Timeout period to pickup pending requests.
     */
    public VariableThreadPoolStrategy(
        int initSize,
        int growSize,
        int poolSize,
        int queueSize,
        int timeout)
    {
        busySize_ = 0;
        pendingSize_ = 0;
        initSize_ = initSize;
        growSize_ = growSize;
        poolSize_ = poolSize;
        timeout_ = timeout;
        runnable_ = new VariableThreadPoolRunnable();
        requestQueue_ = new BoundedBuffer(queueSize);

        // Create the initial threads with a runnable that never times out.
        Runnable initRunnable = new InitVariableThreadPoolRunnable();
        currentSize_ = createThreads(initSize_, initRunnable).length;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Publish the request by putting it on the request queue. If the queue is
     * full, the calling thread is blocked until a slot becomes available. If
     * no threads are available to process this request, create additional
     * threads.
     * 
     * @param request Request to publish.
     * @param result Holds the request result.
     */
    public synchronized void service(IThreadable request, ReturnValue result)
    {
        try
        {
            requestQueue_.put(new Task(request, result));
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
        ++pendingSize_;

        if (busySize_ + pendingSize_ >= currentSize_)
        {
            int growSize = Math.min(poolSize_ - currentSize_, growSize_);
            growSize = createThreads(growSize, runnable_).length;
            currentSize_ += growSize;
        }
    }


    /**
     * Publish a null request for each thread in the pool to signal shutdown.
     */
    public void destroy()
    {
        for (int i = 0; i < currentSize_; ++i)
            try
            {
                requestQueue_.put(SHUTDOWN_TASK);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
    }

    //--------------------------------------------------------------------------
    // VariableThreadPoolRunnable
    //--------------------------------------------------------------------------
    
    /**
     * Strategy specific runnable for VariableThreadPool strategy.
     */
    class VariableThreadPoolRunnable implements Runnable
    {
        /**
         * Returns the next request or times out.
         *
         * @return Next request to proecess.
         * @throws TimeoutException on timeout.
         * @throws InterruptedException on interruption.
         */
        protected Object take() throws TimeoutException, InterruptedException
        {
            return requestQueue_.poll(timeout_);
        }


        /**
         * Process requests unitil the queue is empty or timeout occurs.
         */
        public void run()
        {
            try
            {
                Task task = null;

                while ((task = (Task) take()) != SHUTDOWN_TASK)
                {
                    try
                    {
                        ++busySize_;
                        --pendingSize_;
                        setStarted(task.result_);
                        setResult(task.result_, process(task.request_));
                    }
                    catch (Exception e)
                    {
                        setResult(task.result_, e);
                    }
                    finally
                    {
                        --busySize_;
                    }
                }
            }
            catch (InterruptedException e)
            {
                ; // ignore interruptions
            }
            finally
            {
                --currentSize_;
            }
        }
    }

    //--------------------------------------------------------------------------
    // InitVariableThreadPoolRunnable
    //--------------------------------------------------------------------------
    
    /**
     * Specialized VariableThreadPool runnable that never times out.
     */
    class InitVariableThreadPoolRunnable extends VariableThreadPoolRunnable
    {
        /**
         * Returns the next request.
         *
         * @return Next request to proecess.
         * @throws TimeoutException on timeout.
         * @throws InterruptedException when interrupted.
         */
        protected Object take() throws TimeoutException, InterruptedException
        {
            return requestQueue_.take();
        }
    }
 
    //--------------------------------------------------------------------------
    // Task
    //--------------------------------------------------------------------------
    
    /**
     * A Task encapsulates a request and its result.
     */
    static class Task
    {
        /**
         * Request that is dispatched on a thread.
         */
        private IThreadable request_;
        
        /**
         * Return value of the request.
         */
        private ReturnValue result_;

        
        /**
         * Creates a Task.
         */
        public Task()
        {
        }
        
        
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
}