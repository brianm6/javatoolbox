package toolbox.util.clock;

import java.util.Calendar;
import java.util.Date;

/**
 * AdjustableClock is a clock that can be adjusted to any time.
 */
public class AdjustableClock implements Clock
{
    // TODO: Write unit test
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Difference between this clocks current time and the actual time in 
     * milliseconds.
     */
    private long delta_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AdjustableClock.
     */
    public AdjustableClock()
    {
        delta_ = 0;
    }

    //--------------------------------------------------------------------------
    // Clock Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.clock.Clock#getTime()
     */
    public Date getTime()
    {
        Date d = new Date();
        d.setTime(d.getTime() + delta_);
        return d;
    }


    /**
     * @see toolbox.util.clock.Clock#setTime(java.util.Date)
     */
    public void setTime(Date newTime)
    {
        delta_ = newTime.getTime() - getTime().getTime();
    }


    /**
     * @see toolbox.util.clock.Clock#getCalendar()
     */
    public Calendar getCalendar()
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getTime().getTime());
        return c;
    }


    /**
     * @see toolbox.util.clock.Clock#fastForward(long)
     */
    public void fastForward(long millis)
    {
        delta_ += millis;
    }


    /**
     * @see toolbox.util.clock.Clock#reverse(long)
     */
    public void reverse(long millis)
    {
        fastForward(-millis);
    }
}