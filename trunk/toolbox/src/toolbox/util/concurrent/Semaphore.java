package toolbox.util.concurrent;

/**
 * Base class for counting semaphores.
 * Conceptually, a semaphore maintains a set of permits.
 * Each acquire() blocks if necessary
 * until a permit is available, and then takes it. 
 * Each release adds a permit. However, no actual permit objects
 * are used; the Semaphore just keeps a count of the number
 * available and acts accordingly.
 * <p>
 * A semaphore initialized to 1 can serve as a mutual exclusion
 * lock. 
 * <p>
 * Different implementation subclasses may provide different
 * ordering guarantees (or lack thereof) surrounding which
 * threads will be resumed upon a signal.
 * <p>
 * The default implementation makes NO 
 * guarantees about the order in which threads will 
 * acquire permits. It is often faster than other implementations.
 * <p>
 * <b>Sample usage.</b> Here is a class that uses a semaphore to
 * help manage access to a pool of items.
 * <pre>
 * 
 * class Pool 
 * {
 *   static final MAX_AVAILABLE = 100;
 *   private final Semaphore available = new Semaphore(MAX_AVAILABLE);
 *   
 *   public Object getItem() throws InterruptedException 
 *   { 
 *     // no synch
 *     available.acquire();
 *     return getNextAvailableItem();
 *   }
 *
 *   public void putItem(Object x) 
 *   { 
 *     // no synch
 *     if (markAsUnused(x))
 *       available.release();
 *   }
 *
 *   // Not a particularly efficient data structure; just for demo
 *
 *   protected Object[] items = ... whatever kinds of items being managed
 *   protected boolean[] used = new boolean[MAX_AVAILABLE];
 *
 *   protected synchronized Object getNextAvailableItem() 
 *   { 
 *     for (int i = 0; i < MAX_AVAILABLE; ++i) 
 *     {
 *       if (!used[i]) 
 *       {
 *          used[i] = true;
 *          return items[i];
 *       }
 *     }
 *     return null; // not reached 
 *   }
 *
 *   protected synchronized boolean markAsUnused(Object item) 
 *   { 
 *     for (int i = 0; i < MAX_AVAILABLE; ++i) 
 *     {
 *       if (item == items[i]) 
 *       {
 *          if (used[i]) 
 *          {
 *            used[i] = false;
 *            return true;
 *          }
 *          else
 *            return false;
 *       }
 *     }
 *     return false;
 *   }
 * }
 * </pre>
 * <p>
 */
public class Semaphore implements Sync
{
    /** 
     * Current number of available permits
     */
    private long permits_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Create a Semaphore with the given initial number of permits.
     * Using a seed of one makes the semaphore act as a mutual exclusion lock.
     * Negative seeds are also allowed, in which case no acquires will proceed
     * until the number of releases has pushed the number of permits past 0.
     * 
     * @param  initialPermits  Initial permits
     */
    public Semaphore(long initialPermits)
    {
        permits_ = initialPermits;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /** 
     * Wait until a permit is available, and take one 
     * 
     * @throws InterruptedException on interruption
     */
    public void acquire() throws InterruptedException
    {
        if (Thread.interrupted())
            throw new InterruptedException(
                "Thread has been interrupted prior to Semaphore.acquire()");
            
        synchronized (this)
        {
            try
            {
                while (permits_ <= 0)
                    wait();
                --permits_;
            }
            catch (InterruptedException ex)
            {
                notify();
                throw ex;
            }
        }
    }


    /** 
     * Wait at most msecs millisconds for a permit. 
     * 
     * @param  msecs  Timeout in millis
     * @return True if successful, false otherwise
     * @throws InterruptedException on interruption
     */
    public boolean attempt(long msecs) throws InterruptedException
    {
        if (Thread.interrupted())
            throw new InterruptedException(
                "Thread has been interrupted prior to Semaphore.attempt()");

        synchronized (this)
        {
            if (permits_ > 0)
            {
                --permits_;
                return true;
            }
            else if (msecs <= 0)
            {
                return false;
            }
            else
            {
                try
                {
                    long startTime = System.currentTimeMillis();
                    long waitTime = msecs;

                    for (;;)
                    {
                        wait(waitTime);
                        if (permits_ > 0)
                        {
                            --permits_;
                            return true;
                        }
                        else
                        {
                            waitTime = msecs - 
                                (System.currentTimeMillis() - startTime);
                                
                            if (waitTime <= 0)
                                return false;
                        }
                    }
                }
                catch (InterruptedException ex)
                {
                    notify();
                    throw ex;
                }
            }
        }
    }


    /** 
     * Release a permit 
     */
    public synchronized void release()
    {
        ++permits_;
        notify();
    }


    /** 
     * Release N permits. <code>release(n)</code> is
     * equivalent in effect to:
     * <pre>
     *   for (int i = 0; i < n; ++i) release();
     * </pre>
     * <p>
     * But may be more efficient in some semaphore implementations.
     * 
     * @param     n   Time in millis
     * @exception IllegalArgumentException if n is negative.
     */
    public synchronized void release(long n) throws IllegalArgumentException
    {
        if (n < 0)
            throw new IllegalArgumentException("Negative argument");

        permits_ += n;
        
        for (long i = 0; i < n; ++i)
            notify();
    }


    /**
     * Returns the current number of available permits. Returns an accurate, 
     * but possibly unstable value, that may change immediately after returning.
     * 
     * @return  long
     */
    public synchronized long getPermits()
    {
        return permits_;
    }
}