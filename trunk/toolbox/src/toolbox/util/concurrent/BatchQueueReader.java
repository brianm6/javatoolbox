package toolbox.util.concurrent;

import toolbox.util.ThreadUtil;

/**
 * Reads as much content off a queue as possible (batch mode)
 * and delivers in a single call to execute()
 */
public abstract class BatchQueueReader implements Runnable
{
    /** Queue to read from **/
    private BlockingQueue queue_;
    
    /** Exit flag **/
    private boolean continue_ = true;
    
    /** Delay **/
    private int delay_ = 1000;
    
    
    /**
     * Constructor for MultiLinePopper.
     */
    public BatchQueueReader(BlockingQueue queue)
    {
        queue_ = queue;
    }


    /**
     * Execute for each object in queue
     */    
    public abstract void execute(Object obj[]);


    /**
     * Shuts down reader
     */
    public void shutdown()
    {
        continue_ = false;
    }


    /**
     * Runs the reader
     */
    public void run()
    {
        while (continue_)
        {
            try
            {
                //ThreadUtil.sleep(delay_);
                
                Object first = queue_.pull();
                
                int size = queue_.size();
                
                if (size > 0)
                {
                    // Create array with one extra slot for the first
                    Object[] objs = new Object[size+1];
                    
                    // Place first elemnt in array
                    objs[0] = first;
                    
                    // Read the rest from the queue
                    for (int i=1; i<=size; i++)                
                        objs[i] = queue_.pull();
                        
                    execute(objs);
                }
                else
                {
                    execute(new Object[] { first });
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}