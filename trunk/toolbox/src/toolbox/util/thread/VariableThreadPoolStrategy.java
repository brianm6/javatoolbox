package toolbox.util.thread;

import java.util.ArrayList;

import toolbox.util.sync.BoundedBuffer;
import toolbox.util.sync.BoundedBufferAdapter;
import toolbox.util.sync.Timeout;


/**
 * VariableThreadPoolStrategy.java
 *
 * This class implements a variable-thread-strategy that is a combination of
 * thread-per-request at low request frequencies and a thread-pool strategy
 * at high request frequencies.  If no threads are created up front, so the
 * initial requests will incur thread creation penalities.  However, after a
 * thread has finished servicing a request, it will make an effort to service
 * any pending requests before it terminates.  Thus, the same thread can be
 * resused.  This reduces the number of threads to create during bursts of
 * requests.
 */
public class VariableThreadPoolStrategy extends ThreadedDispatcherStrategy
{
    static class Task
    {
        public IThreadable request;
        public ReturnValue result;

        public Task(IThreadable request, ReturnValue result)
        {
            this.request = request;
            this.result = result;
        }
    }


    public static final int DEFAULT_INIT_SIZE = 0;
    public static final int DEFAULT_GROW_SIZE = 5;
    public static final int DEFAULT_POOL_SIZE = 100;
    public static final int DEFAULT_QUEUE_SIZE = 100;
    public static final int DEFAULT_TIMEOUT = 5000;
    private int initSize_;
    private int growSize_;
    private int poolSize_;
    private int pendingSize_;
    private int currentSize_;
    private int busySize_;
    private int timeout_;
    private Runnable runnable_;
    private BoundedBuffer requestQueue_;


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
    * @param    iniSize            the number of threads initially created.
    * @param    growSize    the number of threads per increment.
    * @param    poolSize    the maximum number threads createable.
    * @param    queueSize    the maximum number of buffered requests.
    * @param    timeout        the timeout period to pickup pending requests.
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


    /**
    * Publish the request by putting it on the request queue.  If the
    * queue is full, the calling thread is blocked until a slot becomes
    * available.  If no threads are available to process this request,
    * create additional threads.
    *
    * @param    request        the request to publish.
    * @param    result            holds the request result.
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
    * Publish a null request for each thread in the pool to signal
    * shutdown.
    */
    public void shutdown()
    {
        for (int i = 0; i < currentSize_; ++i)
            requestQueue_.put(null);
    }


    /**
    * Strategy specific runnable for VariableThreadPool strategy.
    */
    class VariableThreadPoolRunnable
        implements java.lang.Runnable
    {

        /**
       * Returns the next request or times out.
       *
       * @return  the next request to proecess.
       */
        protected Object take()
                       throws Timeout, InterruptedException
        {
            return requestQueue_.take(timeout_);
        }


        /*
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
                        setStarted(task.result);
                        setResult(task.result, process(task.request));
                    }
                    catch (Exception e)
                    {
                        setResult(task.result, e);
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
    class InitVariableThreadPoolRunnable
        extends VariableThreadPoolRunnable
    {

        /**
       * Returns the next request.
       *
       * @return  the next request to proecess.
       */
        protected Object take()
                       throws Timeout, InterruptedException
        {
            return requestQueue_.take();
        }
    }
}