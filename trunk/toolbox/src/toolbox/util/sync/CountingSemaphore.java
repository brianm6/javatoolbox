package toolbox.util.sync;

/**
 * This class implements a counting semaphore.
**/
public class CountingSemaphore
{
    int count_;
    int maximum_;
    EventSemaphore event_;

    public CountingSemaphore()
    {
        this(0);
    }

    public CountingSemaphore(int initial)
    {
        this(initial, Integer.MAX_VALUE);
    }

    public CountingSemaphore(int initial, int maximum)
    {
        if (initial < 0 || maximum < 0 || 
            initial > maximum)
            throw new IllegalArgumentException();

        event_ = new EventSemaphore();
        count_ = initial;
        maximum_ = maximum;
    }

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

    public void obtain()
    {
        lock();
    }

    public synchronized boolean tryLock(int how_many)
    {
        if (count_ >= how_many)
        {
            count_ -= how_many;

            return true;
        }

        return false;
    }

    public boolean tryLock()
    {
        return tryLock(1);
    }

    public synchronized void unlock(int how_many)
    {
        if (how_many < 0 || 
            (count_ + how_many > maximum_))
            throw new IllegalArgumentException();

        count_ += how_many;
        event_.post();
    }

    public void unlock()
    {
        unlock(1);
    }

    public void release(int how_many)
    {
        unlock(how_many);
    }

    public void release()
    {
        release(1);
    }

    public synchronized int count()
    {
        return count_;
    }

    public synchronized int maximum()
    {
        return maximum_;
    }
}
;