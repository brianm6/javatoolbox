package toolbox.util.thread;

/**
 * ThreadContainer
 *
 * This class groups related threads.
 */
public class ThreadContainer extends ThreadGroup
{
    /**
     * Constructs a new thread container.
     */
    public ThreadContainer()
    {
        super("");
    }


    /**
     * Creates a new thread encapsualting the behavior in runnable.
     *
     * @param    runnable     the runnable to run in the thread.
     * @return   Newly created thread
     */
    public Thread createThread(Runnable runnable)
    {
        return new Thread(this, runnable);
    }
}