package toolbox.util.thread.concurrent;

/**
 * This class implements a counting semaphore.
 */
public class CountingSemaphore
{
    private int count_;
    private int maximum_;
    private EventSemaphore event_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public CountingSemaphore()
    {
        this(0);
    }

    /**
     * Constructor with initial count
     * 
     * @param  initial  Initial count
     */
    public CountingSemaphore(int initial)
    {
        this(initial, Integer.MAX_VALUE);
    }

    /**
     * Constructor with count and max count
     * 
     * @param  initial  Initial count
     * @param  maximum  Max count
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
     * Obtains lock
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
     * Obtains a lock
     */
    public void obtain()
    {
        lock();
    }

    /**
     * Tries to obtail a lock
     * 
     * @param  howMany  Count to obtail
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
     * Tries to obtain the lock
     * 
     * @return  True if successful, false otherwise
     */
    public boolean tryLock()
    {
        return tryLock(1);
    }

    /**
     * Releases the lock
     * 
     * @param  howMany  Count to unlock
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
     * Unlocks
     */
    public void unlock()
    {
        unlock(1);
    }

    /**
     * Releases lock
     * 
     * @param  howMany  Count of how many to release
     */
    public void release(int howMany)
    {
        unlock(howMany);
    }

    /**
     * Releases a lock
     */
    public void release()
    {
        release(1);
    }

    /**
     * @return Count of semaphore
     */
    public synchronized int count()
    {
        return count_;
    }

    /**
     * @return  Max count
     */
    public synchronized int maximum()
    {
        return maximum_;
    }
}