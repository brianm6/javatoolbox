package toolbox.util.thread.concurrent;

/**
 * Mutex.java
 *
 * This class implements a recursive mutex.
 */
public class Mutex
{
    int count_ = 0;
    Thread owner_ = null;

    public static class NotOwnerException
        extends RuntimeException
    {
    }

    ;public static class UnderflowException
        extends RuntimeException
    {
    }

    ;
    /**
    * Constructs a new mutex in the unlocked state. 
    */
    public Mutex()
    {
        this(false);
    }


    /**
    * Constructs a new mutex in the 'locked' state.
    *
    * @param    locked        true if the thread creating this mutex
    *                should lock it on creation, false otherwise.
    */
    public Mutex(boolean locked)
    {
        if (locked)
            lock();
    }


    /**
    * Attempt to obtain the mutex.  The calling thread will block if a 
    * different thread already aquires the mutex.  Since this lock is
    * recursive, the same thread may obtain it multiple times.
    */
    public synchronized void lock()
    {
        if (owner_ != Thread.currentThread())
        {
            while (owner_ != null)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException ie)
                {

                    // ignore
                }
            }

            owner_ = Thread.currentThread();
        }

        count_++;
    }


    /**
    * Alternate call for 'lock'.
    */
    public void obtain()
    {
        lock();
    }


    /**
    * Attempt to release the mutex.
    *
    * @exception    UnderflowException if mutex has not been acquired.
    * @exception    NotOwnerException if calling thread does not own mutex.
    */
    public synchronized void unlock()
    {
        if (count_ <= 0)
        {
            throw new UnderflowException();
        }
        else if (owner_ != Thread.currentThread())
        {
            throw new NotOwnerException();
        }
        else if (--count_ == 0)
        {
            owner_ = null;
            notify();
        }
    }


    /**
    * Alternate call for 'release'.
    */
    public void release()
    {
        unlock();
    }


    /**
    * Attempt to obtain the mutex.  The calling thread return flase if a 
    * different thread already aquires the mutex.  Since this lock is
    * recursive, the same thread may obtain it multiple times.
    *
    * @return    true if thread was able to aquire the mutex, false otherwise.
    */
    public synchronized boolean tryLock()
    {
        if (owner_ == null)
        {
            owner_ = Thread.currentThread();
        }
        else if (owner_ != Thread.currentThread())
        {
            return false;
        }

        count_++;

        return true;
    }
}