package toolbox.util.concurrent;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.util.concurrent.Semaphore;
import edu.emory.mathcs.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ElapsedTime;

/**
 * Queue that blocks on calls to pull() until an element is available.
 */
public class BlockingQueue
{
    private static final Logger logger_ = Logger.getLogger(BlockingQueue.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * The backing store for the queue.
     */        
    private List queue_;
    
    /**
     * Mutex semaphore.
     */
    private Semaphore semaphore_;
    
    /**
     * Mutex.
     */
    private Semaphore mutex_;

    //--------------------------------------------------------------------------
    // Constuctors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a BlockingQueue.
     */
    public BlockingQueue()
    {
        semaphore_ = new Semaphore(0);
        queue_     = new ArrayList(50);
        mutex_     = new Semaphore(1);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
        
    /**
     * Pulls element off the queue. Blocks until an element is available if the
     * queue is empty.
     * 
     * @return Next element.
     * @throws InterruptedException on error.
     */
    public Object pull() throws InterruptedException
    {
        try
        {
            try
            {
                semaphore_.acquire();
                mutex_.acquire();
                Object obj = queue_.remove(0);

                return obj;
            }
            finally
            {
                mutex_.release();
            }
        }
        catch (InterruptedException e)
        {
            throw e;
        }
    }

    
    /**
     * Pulls element off the queue. Blocks until an element is available if the
     * queue is empty.
     * 
     * @param millis Timeout in milliseconds.
     * @return Next element.
     * @throws InterruptedException on error.
     */
    public Object pull(long millis) throws InterruptedException
    {
        try
        {
            try
            {
                ElapsedTime timeUsed = new ElapsedTime();
                
                if (semaphore_.tryAcquire(millis, TimeUnit.MILLISECONDS))
                {
                    timeUsed.setEndTime();
                    millis -= timeUsed.getTotalMillis();
                    
                    if (mutex_.tryAcquire(millis, TimeUnit.MILLISECONDS))
                    {
                        Object obj = queue_.remove(0);
                        return obj;
                    }
                    else
                        return null;
                }
                else
                    return null;
            }
            finally
            {
                mutex_.release();
            }
        }
        catch (InterruptedException e)
        {
            throw e;
        }
    }

    
    /**
     * Pushes an element onto the queue.
     * 
     * @param obj Object to push onto the queue.
     */
    public void push(Object obj)
    {
        try
        {
            mutex_.acquire();
            queue_.add(obj);
            semaphore_.release();
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        finally
        {
            mutex_.release();
        }
    }

    
    /**
     * Returns the size of the queue.
     * 
     * @return Queue size.
     */
    public int size()
    {
        int size = 0;
        try
        {
            mutex_.acquire();
            size = queue_.size();
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        finally
        {
            mutex_.release();
        }

        return size;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Dumps contents of queue one line at a time.
     * 
     * @return Queue contents as a string, one line per element.
     */
    public String toString()
    {
        return ArrayUtil.toString(queue_.toArray());
    }
}