package toolbox.util.invoker.test;

import java.util.Date;

/**
 * Runnable that allows the amount of time that run() takes to execute to be
 * specified.
 */
public class TimedRunner implements Runnable
{
    /**
     * Number of milliseconds run() should take to execute.
     */
    private long millis_;

    /**
     * Flag for whether run() was executed.
     */
    private boolean wasInvoked_;

    /**
     * Timestamp on entry of invocation.
     */
    private Date begin_;

    /**
     * Timestamp on exit from invocation.
     */
    private Date end_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TimedRunner.
     * 
     * @param millis Number of millis that run() should take to execute
     */
    public TimedRunner(long millis)
    {
        millis_ = millis;
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------

    /**
     * Pauses for millis before returning.
     */
    public void run()
    {
        begin_ = new Date();

        try
        {
            Thread.sleep(millis_);
        }
        catch (InterruptedException ie)
        {
            ; // Ignore
        }

        wasInvoked_ = true;

        end_ = new Date();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns true if the run() method was executed, false otherwise.
     *
     * @return boolean
     */
    public boolean wasInvoked()
    {
        return wasInvoked_;
    }

    
    /**
     * Returns the timestamp on entry to run().
     *
     * @return Date
     */
    public Date getBegin()
    {
        return begin_;
    }

    
    /**
     * Returns the timestamp on exit of run().
     *
     * @return Date
     */
    public Date getEnd()
    {
        return end_;
    }
}