package toolbox.util.thread;

import toolbox.util.ClassUtil;

/**
 * ThreadContainer groups related threads.
 */
public class ThreadContainer extends ThreadGroup
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a new thread container.
     */
    public ThreadContainer()
    {
        super("");
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new thread encapsualting the behavior in runnable.
     *
     * @param runnable Runnable to run in the thread.
     * @return Newly created thread
     */
    public Thread createThread(Runnable runnable)
    {
        return new Thread(this, runnable, 
            ClassUtil.stripPackage(runnable.getClass().getName()));
    }
}