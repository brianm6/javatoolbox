package toolbox.util.thread;

/**
 * ThreadContainer.java
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
     */
    public Thread createThread(java.lang.Runnable runnable)
    {
        return new Thread(this, runnable);
    }
}