package toolbox.util.thread.concurrent;

/**
 * This class implements an event synchronization semaphore.  Threads will
 * block on this semaphore until the desired condition is fulfilled at which
 * time the semaphore will be 'posted'. 
 */
public class EventSemaphore
{
    private Mutex mutex_;
    private boolean posted_;
    private ConditionVariable event_;

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
        event_ = new ConditionVariable();
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
            mutex_.lock();
            event_.condBroadcast();
            mutex_.unlock();
        }
    }


    /**
     * Signals the event semaphore unblocking all waiting threads.  All
     * subsequent waits will block until the event semaphore is posted again. 
     */
    public synchronized void pulse()
    {
        posted_ = false;
        mutex_.lock();
        event_.condBroadcast();
        mutex_.unlock();
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
                mutex_.lock();
        }

        event_.condWait(mutex_);
        mutex_.unlock();
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
                mutex_.lock();
        }

        event_.condWait(mutex_, timeout);
        mutex_.unlock();
    }


    /**
     * Returns true if this event semaphore is in a posted state.
     *
     * @return True if this event semaphore is in a posted state.
     */
    public synchronized boolean posted()
    {
        return posted_;
    }
}