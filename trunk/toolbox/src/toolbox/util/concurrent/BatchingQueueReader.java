package toolbox.util.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

/**
 * Reads as much content off a queue as possible (batch mode) and delivers 
 * in a single call to IBatchingQueueListener.nextBatch()
 */
public class BatchingQueueReader
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
     * Started flag
     */
    private boolean started_ = false;
    
    /** 
     * Queue Listeners 
     */
    private List listeners_ = new ArrayList();    

    /**
     * Worker thread
     */
    private Thread worker_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for BatchingQueueReader
     * 
     * @param  queue  Queue to read in batch mode from
     */
    public BatchingQueueReader(BlockingQueue queue)
    {
        queue_ = queue;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Stops the reader
     * 
     * @throws  IllegalStateException if the reader has already been stopped
     */
    public synchronized void stop() throws IllegalStateException
    {
        if (started_)
        {
            started_  = false;
            ThreadUtil.stop(worker_, 2000);
        }
        else
        {
            throw new IllegalStateException(
                "BatchingQueueReader has already been stopped.");    
        }
    }

    /**
     * Starts the reader
     * 
     * @throws  IllegalStateException if the reader is already started
     */
    public synchronized void start() throws IllegalStateException
    {
        if (!started_)
        {
            started_  = true;
            worker_ = new Thread(new Worker());
            worker_.start(); 
        }
        else
        {
            throw new IllegalStateException(
                "BatchingQueueReader has already been started.");
        }
    }

    //--------------------------------------------------------------------------
    // Event Notification
    //--------------------------------------------------------------------------
 
    /**
     * Fires notification of new batch of elements available
     * 
     * @param  elements  New elements available
     */
    protected synchronized void fireNextBatch(Object[] elements)
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
    public synchronized void addBatchingQueueListener(IBatchingQueueListener listener)
    {
        listeners_.add(listener);
    }
 
    
    /**
     * Removes a listener
     * 
     * @param  listener  Listener to remove
     */
    public synchronized void removeBatchingQueueListener(IBatchingQueueListener listener)
    {
        listeners_.remove(listener);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    class Worker implements Runnable
    {
        /**
         * Runs the reader
         */
        public void run()
        {
            String method = "[run   ] ";
            
            //logger_.debug(method + "Batching queue reader started!");
                    
            while (started_)
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
                            
                        fireNextBatch(objs);
                    }
                    else
                    {
                        fireNextBatch(new Object[] { first });
                    }
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }
    
}