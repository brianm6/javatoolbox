package toolbox.util.thread.strategy;

import java.util.ArrayList;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;
import toolbox.util.thread.concurrent.BoundedBufferAdapter;
import toolbox.util.thread.concurrent.IBoundedBuffer;
import toolbox.util.thread.concurrent.Timeout;


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
    /** 
     * Default initial size 
     */
    public static final int DEFAULT_INIT_SIZE = 0;
    
    /** 
     * Default grow size 
     */
    public static final int DEFAULT_GROW_SIZE = 5;
    
    /** 
     * Default pool size 
     */
    public static final int DEFAULT_POOL_SIZE = 100;
    
    /** 
     * Default queue size 
     */
    public static final int DEFAULT_QUEUE_SIZE = 100;
    
    /** 
     * Default timeout in millis 
     */
    public static final int DEFAULT_TIMEOUT = 5000;
    
    private int initSize_;
    private int growSize_;
    private int poolSize_;
    private int pendingSize_;
    private int currentSize_;
    private int busySize_;
    private int timeout_;
    private Runnable runnable_;
    private IBoundedBuffer requestQueue_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a default variable thread pool.
     */
    public VariableThreadPoolStrategy()
    {
        this(DEFAULT_INIT_SIZE, DEFAULT_GROW_SIZE, DEFAULT_POOL_SIZE, 
             DEFAULT_QUEUE_SIZE, DEFAULT_TIMEOUT);
    }


    /**
     * Creates a default variable thread pool consisting of initSize threads.
     * 
     * @param initSize Initial number of threads
     */
    public VariableThreadPoolStrategy(int initSize)
    {
        this(initSize, DEFAULT_GROW_SIZE, DEFAULT_POOL_SIZE, 
             DEFAULT_QUEUE_SIZE, DEFAULT_TIMEOUT);
    }


    /**
     * Creates a variable thread pool of capacity poolSize, increment 
     * growSize, queueSize and thread timeout period.
     *
     * @param initSize Number of threads initially created.
     * @param growSize Number of threads per increment.
     * @param poolSize Maximum number threads createable.
     * @param queueSize Maximum number of buffered requests.
     * @param timeout Timeout period to pickup pending requests.
     */
    public VariableThreadPoolStrategy(int initSize, int growSize, int poolSize, 
                                      int queueSize, int timeout)
    {
        busySize_ = 0;
        pendingSize_ = 0;
        initSize_ = initSize;
        growSize_ = growSize;
        poolSize_ = poolSize;
        timeout_ = timeout;
        runnable_ = new VariableThreadPoolRunnable();
        requestQueue_ = new BoundedBufferAdapter(new ArrayList(), queueSize);

        // Create the initial threads with a runnable that never times out.
        Runnable initRunnable = new InitVariableThreadPoolRunnable();
        currentSize_ = createThreads(initSize, initRunnable).length;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Publish the request by putting it on the request queue.  If the queue is 
     * full, the calling thread is blocked until a slot becomes available.  If 
     * no threads are available to process this request, create additional 
     * threads.
     *
     * @param request Request to publish.
     * @param result Holds the request result.
     */
    public synchronized void service(IThreadable request, ReturnValue result)
    {
        requestQueue_.put(new Task(request, result));
        ++pendingSize_;

        if (busySize_ + pendingSize_ >= currentSize_)
        {
            int growSize = Math.min(poolSize_ - 
                                    currentSize_, growSize_);
            growSize = createThreads(growSize, runnable_).length;
            currentSize_ += growSize;
        }
    }


    /**
     * Publish a null request for each thread in the pool to signal shutdown
     */
    public void shutdown()
    {
        for (int i = 0; i < currentSize_; ++i)
            requestQueue_.put(null);
    }

    //--------------------------------------------------------------------------
    // Inner Classes
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
         */
        protected Object take()
                       throws Timeout, InterruptedException
        {
            return requestQueue_.take(timeout_);
        }


        /**
         * Process requests unitil the queue is empty or timeout occurs.
         */
        public void run()
        {
            try
            {
                Task task = null;

                while ((task = (Task)take()) != null)
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
                // ignore interruptions
            }
            catch (Timeout t)
            {
                // no requests pending
            }
            finally
            {
                --currentSize_;
            }
        }
    }


    /**
     * Specialized VariableThreadPool runnable that never times out.
     */
    class InitVariableThreadPoolRunnable extends VariableThreadPoolRunnable
    {
        /**
         * Returns the next request.
         *
         * @return Next request to proecess.
         */
        protected Object take() throws Timeout, InterruptedException
        {
            return requestQueue_.take();
        }
    }
 
    
    /**
     * A Task encapsulates a request and its result.
     */
    static class Task
    {
        private IThreadable request_;
        private ReturnValue result_;

        public Task(IThreadable request, ReturnValue result)
        {
            this.request_ = request;
            this.result_ = result;
        }
    }
}