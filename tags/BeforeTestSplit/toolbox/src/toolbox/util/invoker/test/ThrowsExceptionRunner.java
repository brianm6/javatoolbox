package toolbox.util.invoker.test;

import toolbox.util.ThreadUtil;

/**
 * Runnable that throws an exception after a certain amount of time has passed.
 */
public class ThrowsExceptionRunner implements Runnable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Number of milliseconds to wait before throwing the exception.
     */
    private int delay_;
    
    /**
     * Exception to throw.
     */
    private RuntimeException exception_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ThrowsExceptionRunner.
     *
     * @param exception Exception to throw. 
     * @param delay Delay in millis before throwing an exception.
     */
    public ThrowsExceptionRunner(RuntimeException exception, int delay)
    {
        exception_ = exception;
        delay_ = delay;
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------

    /**
     * Pauses for millis before returning.
     */
    public void run()
    {
        ThreadUtil.sleep(delay_);
        throw exception_;
    }
}