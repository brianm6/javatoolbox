package toolbox.util.thread.strategy;

import edu.emory.mathcs.util.concurrent.ArrayBlockingQueue;
import edu.emory.mathcs.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;

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
    private static final Logger logger_ = 
        Logger.getLogger(ThreadPoolStrategy.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Default pool size.
     */
    public static final int DEFAULT_POOL_SIZE = 10;
    
    /**
     * Default queue size.
     */
    public static final int DEFAULT_QUEUE_SIZE = 100;
    
    /**
     * Static task that is put on the request queue to signify a shutdown.
     */
    public static final ThreadPoolStrategy.Task SHUTDOWN_TASK = 
        new ThreadPoolStrategy.Task();

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Size fo the thread pool.
     */
    private int poolSize_;
    
    /**
     * Runnable.
     */
    private Runnable runnable_;
    
    /**
     * Queue for requests.
     */
    private BlockingQueue requestQueue_;

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
        requestQueue_ = new ArrayBlockingQueue(queueSize); 
        createThreads(poolSize_, runnable_);
    }

    //--------------------------------------------------------------------------
    // Overrides ThreadedDispatcherStrategy
    //--------------------------------------------------------------------------
    
    /**
     * Services the request by putting it on the request queue. If the queue is
     * full, the calling thread is blocked until a slot becomes available.
     * 
     * @see toolbox.util.thread.strategy.ThreadedDispatcherStrategy#service(
     *      toolbox.util.thread.IThreadable, toolbox.util.thread.ReturnValue)
     */
    public void service(IThreadable request, ReturnValue result)
    {
        try
        {
            requestQueue_.put(new Task(request, result));
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractDispatcherStrategy
    //--------------------------------------------------------------------------
    
    /** 
     * Publish a null request for each thread in the pool to signal shutdown.
     * 
     * @see toolbox.util.thread.strategy.AbstractDispatcherStrategy#destroy()
     */
    public void destroy()
    {
        for (int i = 0; i < poolSize_; ++i)
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
    // Task
    //--------------------------------------------------------------------------
        
    /**
     * A Task encapsulates a request and its result.
     */    
    static class Task
    {
        /**
         * Request to complete.
         */
        private IThreadable request_;
        
        /**
         * End result of completing the request.
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

    //--------------------------------------------------------------------------
    // ThreadPoolRunnable
    //--------------------------------------------------------------------------
    
    /**
     * Strategy specific runnable for thread-pool strategy.
     */
    class ThreadPoolRunnable implements Runnable
    {
        /**
         * Process the next available task on the queue or block until one
         * becomes available. A null task instructs this strategy to stop
         * reading further tasks.
         */
        public void run()
        {
            Task task = null;

            try
            {
                // TODO: SHUTDOWN
                while ((task = (Task) requestQueue_.take()) != SHUTDOWN_TASK)
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
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }
    }
}