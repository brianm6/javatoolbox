package toolbox.util.thread.concurrent;

/**
 * This class implements a counting semaphore.
 */
public class CountingSemaphore
{
    /**
     * Current count.
     */
    private int count_;
    
    /**
     * Maximum count.
     */
    private int maximum_;
    
    /**
     * Internal delegate.
     */
    private EventSemaphore event_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CountingSemaphore.
     */
    public CountingSemaphore()
    {
        this(0);
    }

    
    /**
     * Creates a CountingSemaphore with an initial count.
     * 
     * @param initial Initial count
     */
    public CountingSemaphore(int initial)
    {
        this(initial, Integer.MAX_VALUE);
    }

    
    /**
     * Creates a CountingSemaphore with the given options.
     * 
     * @param initial Initial count
     * @param maximum Max count
     */
    public CountingSemaphore(int initial, int maximum)
    {
        if (initial < 0 || maximum < 0 || 
            initial > maximum)
            throw new IllegalArgumentException();

        event_ = new EventSemaphore();
        count_ = initial;
        maximum_ = maximum;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Obtains the lock.
     */
    public void lock()
    {
        while (true)
        {
            synchronized (this)
            {
                if (count_ > 0)
                {
                    if (--count_ == 0)
                        event_.reset();

                    return;
                }
            }

            event_.waitFor();
        }
    }

    
    /**
     * Synonym for lock().
     */
    public void obtain()
    {
        lock();
    }

    
    /**
     * Tries to obtain a lock.
     * 
     * @param howMany Count to obtain
     * @return True if lock obtained, false otherwise
     */
    public synchronized boolean tryLock(int howMany)
    {
        if (count_ >= howMany)
        {
            count_ -= howMany;

            return true;
        }

        return false;
    }

    
    /**
     * Tries to obtain the lock.
     * 
     * @return True if successful, false otherwise
     */
    public boolean tryLock()
    {
        return tryLock(1);
    }

    
    /**
     * Releases the lock.
     * 
     * @param howMany Count to unlock
     */
    public synchronized void unlock(int howMany)
    {
        if (howMany < 0 || 
            (count_ + howMany > maximum_))
            throw new IllegalArgumentException();

        count_ += howMany;
        event_.post();
    }

    
    /**
     * Release a single lock.
     */
    public void unlock()
    {
        unlock(1);
    }

    
    /**
     * Releases lock.
     * 
     * @param howMany Count of how many to release
     */
    public void release(int howMany)
    {
        unlock(howMany);
    }

    
    /**
     * Releases a lock.
     */
    public void release()
    {
        release(1);
    }

    
    /**
     * Returns the semaphore count.
     * 
     * @return int
     */
    public synchronized int count()
    {
        return count_;
    }

    
    /**
     * Returns the maximum count.
     * 
     * @return int
     */
    public synchronized int maximum()
    {
        return maximum_;
    }
}