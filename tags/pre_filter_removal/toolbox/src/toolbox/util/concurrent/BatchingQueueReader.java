package toolbox.util.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.util.concurrent.BlockingQueue;
import edu.emory.mathcs.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.service.Nameable;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * Reads as much content off a queue as possible (batch mode) and delivers in a
 * single call to IBatchingQueueListener.nextBatch().
 */
public class BatchingQueueReader implements Startable, Nameable
{
    private static final Logger logger_ = 
        Logger.getLogger(BatchingQueueReader.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Queue to read elements from.
     */
    private BlockingQueue queue_;

    /**
     * Queue Listeners.
     */
    private List listeners_;

    /**
     * Batch thread.
     */
    private Thread worker_;

    /**
     * Friendly name assigned to the batch thread.
     */
    private String name_;

    /**
     * State machine for this reader's lifecycle.
     */
    private StateMachine machine_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a BatchingQueueReader.
     *
     * @param queue Queue to read in batch mode from.
     */
    public BatchingQueueReader(BlockingQueue queue)
    {
        this(queue, "BatchingQueueReader");
    }


    /**
     * Creates a BatchingQueueReader.
     *
     * @param queue Queue to read in batch mode from.
     * @param name Friendly name assigned to the batch thread.
     */
    public BatchingQueueReader(BlockingQueue queue, String name)
    {
        setName(name);
        queue_ = queue;
        listeners_ = new ArrayList();
        machine_ = ServiceUtil.createStateMachine(this);
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------

    /**
     * Starts the reader.
     *
     * @throws IllegalStateException if the reader is already started.
     * @see toolbox.util.service.Startable#start()
     */
    public synchronized void start() throws IllegalStateException
    {
        machine_.checkTransition(ServiceTransition.START);
        worker_ = new Thread(new Worker(), name_);
        machine_.transition(ServiceTransition.START);
        worker_.start();
    }

    
    /**
     * Stops the reader.
     *
     * @throws IllegalStateException if the reader has already been stopped.
     * @see toolbox.util.service.Startable#stop()
     */
    public synchronized void stop() throws IllegalStateException
    {
        logger_.debug(StringUtil.banner("BatchingQueueReader.stop enter"));
        machine_.checkTransition(ServiceTransition.STOP);
        machine_.transition(ServiceTransition.STOP);
        
        try
        {
            worker_.join(5000);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
        
        logger_.debug(StringUtil.banner("BatchingQueueReader.stop exit"));
    }

    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Event Notification
    //--------------------------------------------------------------------------

    /**
     * Fires notification of new batch of elements available.
     *
     * @param elements New elements available.
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
     * Adds a listener.
     *
     * @param listener Listener to add.
     */
    public synchronized void addBatchingQueueListener(
        IBatchingQueueListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Removes a listener.
     *
     * @param listener Listener to remove.
     */
    public synchronized void removeBatchingQueueListener(
        IBatchingQueueListener listener)
    {
        listeners_.remove(listener);
    }

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }
    
    
    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Worker
    //--------------------------------------------------------------------------

    /**
     * Reads objects off the queue in as large blocks as possible and notifies
     * listeners that the next batch is available.
     */
    class Worker implements Runnable
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            //logger_.debug(method + "Batching queue reader started!");

            while (isRunning())
            {
                try
                {
                    Object first = queue_.poll(500, TimeUnit.MILLISECONDS);
                    //take();

                    if (first != null)
                    {
                        int size = queue_.size();
    
                        if (size > 0)
                        {
                            // Create array with one extra slot for the first
                            Object[] objs = new Object[size + 1];
    
                            // Place first elemnt in array
                            objs[0] = first;
    
                            // Read the rest from the queue
                            for (int i = 1; i <= size; i++)
                                objs[i] = queue_.take();
    
                            fireNextBatch(objs);
                        }
                        else
                        {
                            fireNextBatch(new Object[] {first});
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
            
            //logger_.debug(StringUtil.banner("exiting polling loop in queue reader.."));
        }
    }
}