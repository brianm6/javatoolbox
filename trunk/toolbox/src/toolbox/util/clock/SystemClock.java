package toolbox.util.clock;

import java.util.Calendar;
import java.util.Date;

/**
 * SystemClock is the authority which assumes responsibility for answering the
 * needs of various APIs that previously depended on the build in java System
 * clock. The implementation of SystemClock can be switched out at runtime 
 * (using the Strategy design pattern) so that time in the future or the past 
 * can be simulated just as long as all known code references this instance as 
 * the authority on system time.
 * 
 * @see toolbox.util.clock.ActualTimeClock
 * @see toolbox.util.clock.AdjustableClock 
 */
public class SystemClock implements Clock
{
    // TODO: Write unit test
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
 
    /**
     * Singleton instance of the SystemClock.
     */
    private static Clock instance_;
 
    /**
     * SystemClock uses the strategy pattern to delegate behavior to a concrete
     * instance of Clock. 
     */
    private Clock delegate_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SystemClock.
     * 
     * @param delegate Clock to delegate behavior to.
     */
    private SystemClock(Clock delegate)
    {
        setClock(delegate);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns an instance of the SystemClock.
     * 
     * @return Clock
     */
    public static final Clock getInstance()
    {
        if (instance_ == null)
            setInstance(new SystemClock(new ActualTimeClock()));
            
        return instance_;
    }


    /**
     * Switches out the delegate implementation of the Clock.
     * 
     * @param clock Clock to install as the strategy.
     */
    public void setClock(Clock clock)
    {
        delegate_ = clock;
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Sets the instance of the SystemClock. Do no confuse with the delegate.
     * 
     * @param clock System clock instance.
     */
    private static final void setInstance(Clock clock)
    {
        instance_ = clock;
    }

    //--------------------------------------------------------------------------
    // Clock Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.clock.Clock#fastForward(long)
     */
    public void fastForward(long millis)
    {
        delegate_.fastForward(millis);
    }
    
    
    /**
     * @see toolbox.util.clock.Clock#getCalendar()
     */
    public Calendar getCalendar()
    {
        return delegate_.getCalendar();
    }
   
    
    /**
     * @see toolbox.util.clock.Clock#getTime()
     */
    public Date getTime()
    {
        return delegate_.getTime();
    }
    
    
    /**
     * @see toolbox.util.clock.Clock#reverse(long)
     */
    public void reverse(long millis)
    {
        delegate_.reverse(millis);
    }
    
    
    /**
     * @see toolbox.util.clock.Clock#setTime(java.util.Date)
     */
    public void setTime(Date time)
    {
        delegate_.setTime(time);
    }
}