package toolbox.util.thread;

import org.apache.commons.lang.ClassUtils;

/**
 * ThreadContainer groups related threads.
 */
public class ThreadContainer extends ThreadGroup
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ThreadContainer.
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
     * @return Newly created thread.
     */
    public Thread createThread(Runnable runnable)
    {
        return new Thread(
            this, 
            runnable, 
            ClassUtils.getShortClassName(runnable.getClass()));
    }
}