package toolbox.util.thread.concurrent;

import org.apache.log4j.Logger;

import EDU.oswego.cs.dl.util.concurrent.CondVar;
import EDU.oswego.cs.dl.util.concurrent.Mutex;

/**
 * This class implements an event synchronization semaphore.  Threads will
 * block on this semaphore until the desired condition is fulfilled at which
 * time the semaphore will be 'posted'. 
 */
public class EventSemaphore
{
    private static final Logger logger_ = 
        Logger.getLogger(EventSemaphore.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Mutex.
     */
    private Mutex mutex_;
    
    /**
     * Semaphore posted flag.
     */
    private boolean posted_;
    
    /**
     * Event causing semaphore to be posted.
     */
    private CondVar event_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a new event semaphore in the unposted state. 
     */
    public EventSemaphore()
    {
        this(false);
    }


    /**
     * Constructs a new event semaphore in the 'posted' state.
     *
     * @param posted True if this event semaphore should be posted on creation, 
     *        false otherwise.
     */
    public EventSemaphore(boolean posted)
    {
        posted_ = posted;
        mutex_ = new Mutex();
        event_ = new CondVar(mutex_);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Clears the event semaphore causing all future waiting threads to block
     * until the event semaphore is posted again.
     */
    public synchronized void reset()
    {
        posted_ = false;
    }


    /**
     * Signals the event semaphore unblocking all waiting threads.  All
     * subsequent waits will proceed until the event semaphore is reset.
     */
    public synchronized void post()
    {
        if (!posted_)
        {
            posted_ = true;
            try
            {
                mutex_.acquire();
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
            event_.broadcast();
            mutex_.release();
        }
    }


    /**
     * Signals the event semaphore unblocking all waiting threads.  All
     * subsequent waits will block until the event semaphore is posted again. 
     */
    public synchronized void pulse()
    {
        posted_ = false;
        try
        {
            mutex_.acquire();
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
        event_.broadcast();
        mutex_.release();
    }


    /**
     * Blocks the calling thread until this event semaphore is posted.
     */
    public void waitFor()
    {
        synchronized (this)
        {
            if (posted_)
                return;
            else
                try
                {
                    mutex_.acquire();
                }
                catch (InterruptedException e)
                {
                    logger_.error(e);
                }
        }

        try
        {
            event_.await();
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
        mutex_.release();
    }


    /**
     * Blocks the calling thread until this event semaphore is posted or
     * the supplied timeout elapses.
     *
     * @param timeout Timeout to wait in milliseconds
     * @throws InterruptedException if another thread interrupts a blocked 
     *         thread.
     */
    public void waitFor(long timeout) throws InterruptedException
    {
        synchronized (this)
        {
            if (posted_)
                return;
            else
                mutex_.acquire();
        }

        event_.timedwait(timeout);
        mutex_.release();
    }


    /**
     * Returns true if this event semaphore is in a posted state.
     *
     * @return boolean
     */
    public synchronized boolean posted()
    {
        return posted_;
    }
}