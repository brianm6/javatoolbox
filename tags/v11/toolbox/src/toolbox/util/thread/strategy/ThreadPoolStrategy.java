package toolbox.util.thread.strategy;

import java.util.ArrayList;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;
import toolbox.util.thread.concurrent.BoundedBuffer;
import toolbox.util.thread.concurrent.BoundedBufferAdapter;


/**
 * ThreadPoolStrategy.java
 *
 * This class implements a thread-pool strategy that puts requests on a
 * queue for one or more worker threads to extract.  Since all the threads
 * are created up front, the thread creation penalty is incurred on startup
 * and is thus better suited for bursty situations.  However, since a bounded
 * number of threads is created, once all threads are occupied,or the queue is
 * full,  the sender will block until a thread is available to process it, or
 * the queue becomes unsaturated.
 */
public class ThreadPoolStrategy extends ThreadedDispatcherStrategy
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


    public static final int DEFAULT_POOL_SIZE = 10;
    public static final int DEFAULT_QUEUE_SIZE = 100;
    private int poolSize_;
    private Runnable runnable_;
    private BoundedBuffer requestQueue_;


    /**
    * Creates a default thread pool.
    */
    public ThreadPoolStrategy()
    {
        this(DEFAULT_POOL_SIZE, DEFAULT_QUEUE_SIZE);
    }


    /**
    * Creates a thread pool consisting of poolSize threads and a queue
    * of queueSize.
    *
    * @param    poolSize    the number of threads in the pool.
    * @param    queueSize    the maximum number of buffered requests.
    */
    public ThreadPoolStrategy(int poolSize, int queueSize)
    {
        poolSize_ = poolSize;
        runnable_ = new ThreadPoolRunnable();
        requestQueue_ = new BoundedBufferAdapter(new ArrayList(), queueSize);
        createThreads(poolSize_, runnable_);
    }


    /**
    * Services the request by putting it on the request queue.  If the
    * queue is full, the calling thread is blocked until a slot becomes
    * available.
    *
    * @param    request        the request to publish.
    * @param    result            holds the request result.
    */
    public void service(IThreadable request, ReturnValue result)
    {
        requestQueue_.put(new Task(request, result));
    }


    /**
    * Publish a null request for each thread in the pool to signal
    * shutdown.
    */
    public void shutdown()
    {
        for (int i = 0; i < poolSize_; ++i)
            requestQueue_.put(null);
    }


    /**
    * Strategy specific runnable for thread-pool strategy.
    */
    class ThreadPoolRunnable
        implements java.lang.Runnable
    {

        /*
         * Process the next available task on the queue or block until
         * one becomes available.  A null task instructs this strategy
         * to stop reading further tasks.
         */
        public void run()
        {
            Task task = null;

            while ((task = (Task)requestQueue_.take()) != null)
            {
                try
                {
                    setStarted(task.result);
                    setResult(task.result, process(task.request));
                }
                catch (Exception e)
                {
                    setResult(task.result, e);
                }
            }
        }
    }
}