package toolbox.util;

import java.util.Date;

/**
 * Represents the time elapsed between two instances in time. Useful for 
 * determining the time elapsed for any given method call/operation/unit of
 * work. 
 * <pre>
 *  
 * Usage:
 * 
 * // Create new instance init'ed to current time
 * ElapsedTime et = new ElapsedTime();
 * 
 * // Do some work
 * object.doWork();
 * 
 * // Set the ending time. Current time is the default
 * et.setEndTime();
 * 
 * // Show elapsed time
 * System.out.println(et);
 * 
 * </pre>
 */
public class ElapsedTime
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * 1 millisecond. 
     */
    public static final int MILLI = 1;
    
    /** 
     * 1 second.
     */
    public static final int SECOND = 1000 * MILLI;
    
    /** 
     * 1 minute.
     */
    public static final int MINUTE = 60 * SECOND;
    
    /** 
     * 1 hour.
     */
    public static final int HOUR = 60 * MINUTE;
    
    /** 
     * 1 day.
     */
    public static final int DAY = 24 * HOUR;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Starting time in millis.
     */
    private long startTime_;
    
    /**
     * Ending time in millis.
     */
    private long endTime_;
    
    /**
     * Number of days elapsed.
     */
    private long days_;
    
    /**
     * Number of normalized hours elapsed < 23.
     */
    private int  hours_;
    
    /**
     * Number of normalized minutes elapsed < 60.
     */
    private int  minutes_;
    
    /**
     * Number of normalized seconds elapsed < 60.
     */
    private int  seconds_;
    
    /**
     * Number of normalized milliseconds elapsed < 1000.
     */
    private int  millis_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an elapsed time with the start and end times equal to the 
     * current time.
     */
    public ElapsedTime()
    {
        this(new Date());
    }

    
    /**
     * Creates an elapsed time with the start and end times equal to the start 
     * time.
     * 
     * @param startTime Starting time
     */
    public ElapsedTime(Date startTime)
    {
        this(startTime.getTime(), startTime.getTime());
    }

    
    /**
     * Creates an elapsed time from the given time span.
     * 
     * @param startTime Starting time
     * @param endTime Ending time
     */
    public ElapsedTime(Date startTime, Date endTime)
    {
        this(startTime.getTime(), endTime.getTime());
    }

    
    /**
     * Creates an elapsed time from the given time span.
     * 
     * @param startTime Staring time in milliseconds
     * @param endTime Ending time in milliseconds
     */
    public ElapsedTime(long startTime, long endTime)
    {
        startTime_ = startTime;
        endTime_   = endTime;
        recalc();
    }
    
    
    /**
     * Creates an elapsed time from the given time components.
     * 
     * @param days Number of days elapsed           [0..Integer.MAXINT]
     * @param hours Number of hours elapsed         [0..23]
     * @param minutes Number of minutes elapsed     [0..59]
     * @param seconds Number of seconds elapsed     [0..59]
     * @param millis Number of milliseconds elapsed [0..999]
     */
    public ElapsedTime(long days, int hours, int minutes, int seconds, 
        int millis)
    {
        days_    = days;
        hours_   = hours;
        minutes_ = minutes;
        seconds_ = seconds;        
        millis_  = millis;
    }        
    
    
    /** 
     * Creates a copy of the given elapsed time.
     * 
     * @param elapsedTime Elapsed time to copy
     */
    public ElapsedTime(ElapsedTime elapsedTime)
    {
        this(elapsedTime.getDays(), elapsedTime.getHours(),
            elapsedTime.getMinutes(), elapsedTime.getSeconds(), 
            elapsedTime.getMillis());        
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Accessor for the number of days elapsed [0..Integer.MAXINT].
     * 
     * @return Days elapsed
     */    
    public long getDays()
    {
        return days_;
    }


    /**
     * Accessor for the number of hours elapsed [0..23].
     * 
     * @return Hours elapsed
     */    
    public int getHours()
    {
        return hours_;
    }

    /**
     * Accessor for the number of minutes elapsed [0..59].
     * 
     * @return Minutes elapsed
     */    
    public int getMinutes()
    {
        return minutes_;
    }

    /**
     * Accessor for the number of seconds elapsed [0..59].
     * 
     * @return Seconds elapsed
     */    
    public int getSeconds()
    {
        return seconds_;
    }


    /**
     * Accessor for the number of milliseconds elapsed [0..999].
     * 
     * @return Milliseconds elapsed
     */
    public int getMillis()
    {
        return millis_;
    }

    
    /**
     * Accessor for the staring time of the elapsed time.
     * 
     * @return Starting time
     */
    public Date getStartTime()
    {
        return new Date(startTime_);
    }
    
    
    /**
     * Accessor for the ending time of the elapsed time.
     * 
     * @return Ending time
     */
    public Date getEndTime()
    {
        return new Date(endTime_);
    }

    
    /**
     * Sets the starting time for the elapsed time.
     *
     * @param startTime Starting time
     */
    public void setStartTime(Date startTime)
    {
        startTime_ = startTime.getTime();
    }
    
    
    /**
     * Sets the starting time for the elapsed time to the current time.
     */
    public void setStartTimeNow()
    {
        startTime_ = new Date().getTime();
    }
    
    
    /**
     * Sets the ending time for the elapsed time.
     * 
     * @param endTime Ending time
     */
    public void setEndTime(Date endTime)
    {
        endTime_ = endTime.getTime();    
        recalc();
    }

    
    /**
     * Sets the ending time for the elapsed time to the current time.
     */
    public void setEndTime()
    {
        endTime_ = new Date().getTime();
        recalc();
    }
    
    
    /**
     * Returns the total time elapsed in milliseconds.
     * 
     * @return long
     */
    public long getTotalMillis()
    {
        int total = 0;
        
        if (days_ > 0)
            total += days_ * DAY * HOUR * MINUTE * SECOND * MILLI;
            
        if (hours_ > 0)
            total += hours_ * HOUR * MINUTE * SECOND * MILLI;
            
        if (minutes_ > 0)
            total += minutes_ * MINUTE * SECOND * MILLI;
            
        if (seconds_ > 0)
            total += seconds_ * SECOND * MILLI;
            
        if (millis_ > 0)
            total += millis_;            
        
        return total;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Calculates the elapsed time between the starting time and ending time.
     */
    protected void recalc()
    {
        Assert.isTrue(
            endTime_ >= startTime_, 
            "Ending time " + endTime_ + 
            " must be greater than or equal to the" +
            "starting time " + startTime_);
        
        long delta;
         
        delta     = endTime_ - startTime_;
        days_     = delta/DAY;
        delta     = delta - (days_ * DAY);
        hours_    = (int)(delta/HOUR);
        delta     = delta - (hours_ * HOUR);
        minutes_  = (int)(delta/MINUTE);
        delta     = delta - (minutes_ * MINUTE);
        seconds_  = (int)(delta/SECOND);
        delta     = delta - (seconds_ * SECOND);
        millis_   = (int)delta;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
        
    /**
     * Returns elapsed time as a formatted string: 3d 23h 34m 23s 897ms.
     * 
     * @return Elapsed time as a formatted string
     */
    public String toString()        
    {
        StringBuffer sb = new StringBuffer();
        
        if (getDays() > 0)
            sb.append(getDays() + "d ");
            
        if (getHours() > 0)
            sb.append(getHours() + "h ");
            
        if (getMinutes() > 0)
            sb.append(getMinutes() + "m ");
            
        if (getSeconds() > 0)
            sb.append(getSeconds() + "s ");
            
        if (getMillis() > 0)
            sb.append(getMillis() + "ms");
        
        return sb.toString().trim();
    }

    
    /**
     * Compares elapsed times for the span of time regardless of the start or 
     * ending time.
     * 
     * @param obj Elapsed time to compare
     * @return True if elapsed time is equals, false otherwise
     */    
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else 
            return (obj instanceof ElapsedTime && 
                    toString().equals(obj.toString()));
    }
    
    
    /**
     * Override hashcode implementation to match equals().
     * 
     * @return Unique hashcode
     */
    public int hashCode()
    {
        return toString().hashCode();
    }
}