package toolbox.util.clock;

import java.util.Calendar;
import java.util.Date;

/**
 * ActualTimeClock is an immutable clock that represends the current time as 
 * reported by the virtual machine.
 */
public class ActualTimeClock implements Clock
{
    // TODO: Write unit test
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActualTimeClock.
     */
    public ActualTimeClock()
    {
    }

    //--------------------------------------------------------------------------
    // Clock Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.clock.Clock#getTime()
     */
    public Date getTime()
    {
        return new Date();
    }


    /**
     * @see toolbox.util.clock.Clock#setTime(java.util.Date)
     */
    public void setTime(Date systemDate)
    {
        throw new IllegalStateException("Cannot change the actual time.");
    }


    /**
     * @see toolbox.util.clock.Clock#getCalendar()
     */
    public Calendar getCalendar()
    {
        return Calendar.getInstance();
    }


    /**
     * @see toolbox.util.clock.Clock#fastForward(long)
     */
    public void fastForward(long millis)
    {
        throw new IllegalStateException("Cannot fast forward the actual time.");
    }


    /**
     * @see toolbox.util.clock.Clock#reverse(long)
     */
    public void reverse(long millis)
    {
        throw new IllegalStateException("Cannot reverse the actual time.");
    }
}