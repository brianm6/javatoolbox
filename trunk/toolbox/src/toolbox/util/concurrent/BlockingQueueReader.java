package toolbox.util.concurrent;

/**
 * Blocking queue reader
 */
public abstract class BlockingQueueReader implements Runnable
{
    private BlockingQueue _queue;
    private boolean _continueProcessing = true;

    /**
     * Execute for each object in queue
     */    
    public abstract void execute(Object obj);

    /**
     * Init
     */
    public void init(BlockingQueue queue)
    {
        _queue = queue;
    }

    /**
     * Shuts down reader
     */
    public void shutdown()
    {
        _continueProcessing = false;
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
                Object obj = _queue.pull();
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