package toolbox.util.thread.concurrent;

/**
 * This class implements a synchronization semaphore that blocks calling
 * threads until a particular condition is met.  It is used in conjuction 
 * with a mutex to ensure condition integrity.
 */
public class ConditionVariable
{
    private Thread owner_ = null;

    /**
     * Releases the mutex and blocks the calling thread until the condition
     * variable is signaled.
     *
     * @param    mutex        mutex used to test the condition.
     * @throws   Mutex.NotOwnerException if the calling thread does not own 
     *           the mutex.
     * @throws   Mutex.UnderflowException if the mutex has not been acquired.
     */
    public void condWait(Mutex mutex) throws Mutex.NotOwnerException, 
        Mutex.UnderflowException
    {
        try
        {
            synchronized (this)
            {
                mutex.unlock();
                wait();
            }
        }
        catch (InterruptedException ie)
        {
            // Allow spurious interrupts
        }
        finally
        {
            // *Always* lock before returning
            mutex.lock();
        }
    }


    /**
     * Releases the mutex and blocks the calling thread until the condition
     * variable is signaled or the timeout period elapses.
     *
     * @param    mutex        Mutex used to test the condition.
     * @param    timeout      Maximum time to wait in milliseconds.
     * @throws   Mutex.NotOwnerException if the calling thread does
     *           not own the mutex.
     * @throws   Mutex.UnderflowException if the mutex has not been
     *           acquired. 
     * @throws   InterruptedException if another thread interrupts
     *           a blocked thread.
     */
    public void condWait(Mutex mutex, long timeout) throws 
        Mutex.NotOwnerException, Mutex.UnderflowException, InterruptedException
    {
        try
        {
            synchronized (this)
            {
                mutex.unlock();
                wait(timeout);
            }
        }
        finally
        {
            // *Always* lock before returning
            mutex.lock();
        }
    }


    /**
     * Signals a single thread blocked on this condition variable. 
     */
    public synchronized void condSignal()
    {
        notify();
    }


    /**
     * Signals all threads blocked on this condition variable. 
     */
    public synchronized void condBroadcast()
    {
        notifyAll();
    }
}