package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class that deals with both the DATE and TIME portions of the 
 * java.util.Date object
 * 
 * TODO: Revisit chaining vs. confusing
 */
public final class DateTimeUtil
{
    // Clover private constructor workaround
    static { new DateTimeUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction
     */
    private DateTimeUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @param   d  Date to format
     * @return  Date/time in dashed MM-dd-yyyy hh:mm a format
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        return df.format(d);
    }
    
    /**
     * Adds individual time components to a date. The original date is returned 
     * for chaining.
     * 
     * @param   date     Date to modify
     * @param   years    Number of years to add
     * @param   months   Number of months to add
     * @param   days     Number of days to add
     * @param   hours    Number of hours to add
     * @param   minutes  Number of minutes to add
     * @param   seconds  Number of seconds to add
     * @return  Reference to modified passed in date
     */
    public static Date add(Date date, int years, int months, int days,
        int hours, int minutes, int seconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, +years);
        cal.add(Calendar.MONTH, +months);
        cal.add(Calendar.DATE, +days);
        cal.add(Calendar.HOUR, +hours);
        cal.add(Calendar.MINUTE, +minutes);
        cal.add(Calendar.SECOND, +seconds);
        date.setTime(cal.getTime().getTime());
        return date;
    }
    
    /**
     * Gets the date/time of the beginning of the current day
     * 
     * @return  Date
     */
    public static Date getBeginningOfDay()
    {
        return getBeginningOfDay(new Date());
    }
    
    /**
     * Convenience method to get the time of day immediately after is rolled 
     * from the previous day.  Time will be 00:00:00 0ms
     * 
     * @param  d  Date to get beginning of
     * @return Date set to the beginning of the day
     */
    public static Date getBeginningOfDay(final Date d)
    {
        Date start = new Date(d.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Get earliest notion of java time
     * 
     * @return Beginning of time
     */
    public static Date getBeginningOfTime()
    {
        return new Date(0);
    }
    
    /**
     * Convenience method to get the time of the current day preset to 
     * 23:59:59 999 ms
     * 
     * @return End of current day
     */
    public static Date getEndOfDay()
    {
        return getEndOfDay(new Date());
    }

    /**
     * Convenience method to get the time of the given day immediately 
     * before it rolls over to the next day.
     * 
     * @param   d  Date to modify
     * @return  Modified date for chaining
     */
    public static Date getEndOfDay(final Date d)
    {
        Date end = new Date(d.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
    
    /**
     * Gets a date far out in the future
     * 
     * @return  Date representing end of time
     */
    public static Date getEndOfTime()
    {
        Calendar c = Calendar.getInstance();
        c.set(99999, 1, 1);
        return c.getTime();
    }
}

/*
     Symbol   Meaning                 Presentation        Example
     ------   -------                 ------------        -------
     G        era designator          (Text)              AD
     y        year                    (Number)            1996
     M        month in year           (Text & Number)     July & 07
     d        day in month            (Number)            10
     h        hour in am/pm (1~12)    (Number)            12
     H        hour in day (0~23)      (Number)            0
     m        minute in hour          (Number)            30
     s        second in minute        (Number)            55
     S        millisecond             (Number)            978
     E        day in week             (Text)              Tuesday
     D        day in year             (Number)            189
     F        day of week in month    (Number)            2 (2nd Wed in July)
     w        week in year            (Number)            27
     W        week in month           (Number)            2
     a        am/pm marker            (Text)              PM
     k        hour in day (1~24)      (Number)            24
     K        hour in am/pm (0~11)    (Number)            0
     z        time zone               (Text)              Pacific Standard Time
     '        escape for text         (Delimiter)
     ''       single quote            (Literal)           '
*/
