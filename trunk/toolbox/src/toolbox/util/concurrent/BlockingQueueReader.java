package toolbox.util.concurrent;

/**
 * Blocking queue reader
 */
public abstract class BlockingQueueReader implements Runnable
{
    private BlockingQueue queue_;
    
    private boolean continueProcessing_ = true;

    /**
     * Execute for each object in queue
     * 
     * @param  obj  Obect to execute
     */    
    public abstract void execute(Object obj);

    /**
     * Init
     * 
     * @param  queue  Queue to read from
     */
    public void init(BlockingQueue queue)
    {
        queue_ = queue;
    }

    /**
     * Shuts down reader
     */
    public void shutdown()
    {
        continueProcessing_ = false;
    }

    /**
     * Runs the reader
     */
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                Object obj = queue_.pull();
                if (obj != null)
                    execute(obj);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}