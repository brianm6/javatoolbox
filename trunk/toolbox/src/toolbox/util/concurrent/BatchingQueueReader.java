package toolbox.util.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Reads as much content off a queue as possible (batch mode) and delivers 
 * in a single call to IBatchQueueListener.nextBatch()
 */
public class BatchingQueueReader implements Runnable
{
    private static final Logger logger_ = 
        Logger.getLogger(BatchingQueueReader.class);
        
    /** 
     * Queue to read elements from
     */
    private BlockingQueue queue_;
    
    /** 
     * Exit flag 
     */
    private boolean continue_ = true;
    
    /** 
     * Queue Listeners 
     */
    private List listeners_ = new ArrayList();    


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for MultiLinePopper.
     */
    public BatchingQueueReader(BlockingQueue queue)
    {
        queue_ = queue;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Shuts down reader
     */
    public void shutdown()
    {
        continue_ = false;
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Runs the reader
     */
    public void run()
    {
        String method = "[run   ] ";
        
        logger_.debug(method + "Batching queue reader started!");
                
        while (continue_)
        {
            try
            {
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
                        
                    fireNotify(objs);
                }
                else
                {
                    fireNotify(new Object[] { first });
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    //--------------------------------------------------------------------------
    // Event Notification
    //-------------------------------------------------------------------------- 
 
    /**
     * Fires notification of new elements available
     * 
     * @param  elements  New elements available
     */
    public void fireNotify(Object[] elements)
    {
        Iterator i = listeners_.iterator();
        while (i.hasNext())
        {
            IBatchingQueueListener listener = 
                (IBatchingQueueListener) i.next();
                
            listener.nextBatch(elements);
        }    
    }
 
    
    /**
     * Adds a listener 
     * 
     * @param  listener  Listener to add
     */   
    public void addBatchQueueListener(IBatchingQueueListener listener)
    {
        listeners_.add(listener);
    }
 
    
    /**
     * Removes a listener
     * 
     * @param  listener  Listener to remove
     */
    public void removeBatchQueueListener(IBatchingQueueListener listener)
    {
        listeners_.remove(listener);
    }
}