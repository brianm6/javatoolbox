package toolbox.util.sync;

/**
 * ReadWriteSemaphore.java
 *
 * This class implements a synchronization semaphore limits access to a
 * resource to a single writer or multiple readers.
 */
public class ReadWriteSemaphore
{
    private Mutex mutex_;
    private Mutex write_;
    private int readLocks_;
    private int writeLocks_;
    private ConditionVariable condition_;


    /**
   * Constructs a new semaphore with no readers or writers.
   */
    public ReadWriteSemaphore()
    {
        readLocks_ = 0;
        writeLocks_ = 0;
        mutex_ = new Mutex();
        write_ = new Mutex();
        condition_ = new ConditionVariable();
    }


    /**
   * Attempts to obtain a read lock, blocking if a write lock exists.
   * Multiple threads can obtain a read lock.
   */
    public void readLock()
    {
        try
        {
            mutex_.lock();

            boolean locked = false;

            while (!locked)
            {
                if (writeLocks_ == 0)
                {
                    readLocks_++;
                    locked = true;
                }
                else if (write_.tryLock())
                {
                    write_.unlock();
                    readLocks_++;
                    locked = true;
                }
                else
                    condition_.condWait(mutex_);
            }
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Releases the read lock owned by the calling thread.
   */
    public void readUnlock()
    {
        try
        {
            mutex_.lock();

            if (--readLocks_ == 0 && writeLocks_ == 0)
                condition_.condSignal();
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Attempts to obtain a read lock, but does not block if write lock exists.
   *
   * @return    true if the read lock was obtained, false otherwise.
   */
    public boolean tryReadLock()
    {
        try
        {
            mutex_.lock();

            boolean locked = false;

            if (writeLocks_ == 0)
            {
                readLocks_++;
                locked = true;
            }
            else if (write_.tryLock())
            {
                write_.unlock();
                readLocks_++;
                locked = true;
            }

            return locked;
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Attempts to obtain a write lock, blocking if a read lock or write lock 
   * exists.  Only a single thread can own a write lock at any time.
   */
    public void writeLock()
    {
        write_.lock();

        try
        {
            mutex_.lock();

            while (writeLocks_ == 0 && readLocks_ != 0)
                condition_.condWait(mutex_);

            writeLocks_++;
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Releases the write lock owned by the calling thread.
   */
    public void writeUnlock()
    {
        try
        {
            mutex_.lock();

            if (--writeLocks_ == 0)
                condition_.condSignal();

            write_.unlock();
        }
        finally
        {
            mutex_.unlock();
        }
    }


    /**
   * Attempts to obtain a write lock, but does not block if read or write
   * lock exists.
   *
   * @return    true if the write lock was obtained, false otherwise.
   */
    public boolean tryWriteLock()
    {
        boolean locked = false;

        if (write_.tryLock())
        {
            try
            {
                mutex_.lock();

                if (writeLocks_ != 0 || readLocks_ == 0)
                {
                    writeLocks_++;
                    locked = true;
                }
                else
                    write_.unlock();
            }
            finally
            {
                mutex_.unlock();
            }
        }

        return locked;
    }
}