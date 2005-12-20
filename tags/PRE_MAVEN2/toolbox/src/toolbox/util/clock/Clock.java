package toolbox.util.clock;

import java.util.Calendar;
import java.util.Date;

/**
 * Simple clock interface. 
 */
public interface Clock
{
    /**
     * Returns this clock's current time.
     * 
     * @return Date
     */
    Date getTime();
    
    
    /**
     * Sets this clock's current time.
     * 
     * @param newTime The new time.
     */
    void setTime(Date newTime);
    
    
    /**
     * Returns this clock's current calendar.
     * 
     * @return Calendar
     */
    Calendar getCalendar();

    
    /**
     * Fast forwards this clock the given number of milliseconds.
     * 
     * @param millis Number of millis to fast forward this clock.
     */
    void fastForward(long millis);
    
    
    /**
     * Reverses this clock the given number of milliseconds.
     * 
     * @param millis Number of millis to reverse this clock.
     */
    void reverse(long millis);
}