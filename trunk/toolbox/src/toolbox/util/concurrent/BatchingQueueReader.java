package toolbox.util.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Category;

/**
 * Reads as much content off a queue as possible (batch mode) and delivers 
 * in a single call to IBatchQueueListener.nofity()
 */
public class BatchingQueueReader implements Runnable
{
    private static final Category logger_ = 
        Category.getInstance(BatchingQueueReader.class);
        
    /** Queue to read from **/
    private BlockingQueue queue_;
    
    /** Exit flag **/
    private boolean continue_ = true;
    
    /** Listners **/
    private List listeners_ = new ArrayList();    

    
    /**
     * Constructor for MultiLinePopper.
     */
    public BatchingQueueReader(BlockingQueue queue)
    {
        queue_ = queue;
    }


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
 
 
    /**
     * Fires notification of new elements available
     */
    public void fireNotify(Object[] elements)
    {
        Iterator i = listeners_.iterator();
        while (i.hasNext())
        {
            IBatchingQueueListener listener = 
                (IBatchingQueueListener) i.next();
                
            listener.notify(elements);
        }    
    }
 
    
    /**
     * Adds a listener 
     */   
    public void addBatchQueueListener(IBatchingQueueListener listener)
    {
        listeners_.add(listener);
    }
 
    
    /**
     * Removes a listener
     */
    public void removeBatchQueueListener(IBatchingQueueListener listener)
    {
        listeners_.remove(listener);
    }
}