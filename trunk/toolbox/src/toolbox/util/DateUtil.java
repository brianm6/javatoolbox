package toolbox.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class that deals only with the DATE portions of the 
 * java.util.Date object
 * 
 * DESIGN NOTE: All behavior defined on this class applies to only the date
 *              portion of a java.util.Date object. This means day, month, and 
 *              year are the only attributes of a Date used for comparison,
 *              equality, operations, etc. Do not be fooled by the name of 
 *              this class!!! Use DateTimeUtil instead. 
 * 
 */
public class DateUtil
{
    /**
     * Private constructor
     */
    private DateUtil()
    {
    }

    /**
     * @return  Time in hh:mma format. ex: 3:43pm
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        return df.format(d);
    }


    /**
     * Adds a given number of days to a date. 
     * The original date is returned for chaining.
     * 
     * @param   date  Date to add days to
     * @param   days  Number of days to add. Can be unsigned to subtract days.
     * @return  Modified date 
     */
    public static Date addDays(Date date, int days)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date.setTime(c.getTime().getTime());
        return date;
    }


    /**
     * Adds a given number of weeks to a date.
     * The original date is returned for chaining.
     * 
     * @param   date   Date to add weeks to
     * @param   weeks  Number of weeks to add
     * @return  Modified date
     */
    public static Date addWeeks(Date date, int weeks)
    {
        return addDays(date, weeks * 7);
    }

    /**
     * Compares only the date portions of a Date object
     *
     * @param    d1    First date
     * @param    d2    Second date
     *
     * @return   -1 if d1 occurs d2
     *            0 if d1 and d2 are the same day
     *            1 if d1 occurs after d2
     */
    public static int compare(Date d1, Date d2)
    {
        if (equals(d1, d2))
            return 0;
        else if (isBefore(d1, d2))
            return -1;
        else
            return 1;
    }


    /**
     * Copies only the date portion of an existing date. The time
     * portion is zeroed out.
     * 
     * @param  d   Date to copy
     * @return Copy of date with time portion zeroed out
     */
    public static Date copy(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(d.getTime()));
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Gets the beginning of the current month.
     * Time portion is zeroed out.
     *
     * @return  First day of the current month
     */
    public static Date getBeginningOfMonth()
    {
        Date d = getToday();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }


    /**
     * Compares two dates for equality based on year, month, and day
     *
     * @param    date1   First date
     * @param    date2   Second date
     * @return   True if the dates are equal, false otherwise
     */
    public static boolean equals(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return 
            cal1.get(Calendar.YEAR)         == cal2.get(Calendar.YEAR)  &&
            cal1.get(Calendar.MONTH)        == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * Gets the current date with the time portion zeroed out
     * 
     * @return  Todays date
     */
    public static Date getToday()
    {
        return zeroTime(new Date());
    }


    /**
     * Gets the next occurrence of a given day relative to today.
     * Answers the question: What date is next Thursday?
     *
     * @param    day  Calendar.MONDAY, TUE, ...
     * @return   Next occuring date of the given day
     */
    public static Date getNextDay(int day)
    {
        return getNextDay(getToday(), day);
    }


    /**
     * Gets the next occurrence of a given day relative to a date.
     * Answers the question: 
     * What is the date of the first Tuesday after 6/21/2000?
     *
     * @param    fromDate   Date to search from
     * @param    day        Calendar.MONDAY, TUE, ...
     * @return   Next occurring date of the given day from the passed date.
     */
    public static Date getNextDay(final Date fromDate, final int day)
    {
        Date copy = copy(fromDate);
        
        Calendar c = Calendar.getInstance();
        c.setTime(copy);

        // If the search day happens to be the same as day as fromDate, 
        // inch forward a day so we'll find the next occurrence.        
        if (c.get(Calendar.DAY_OF_WEEK) == day)
            c.add(Calendar.DATE, 1);

        while (c.get(Calendar.DAY_OF_WEEK) != day)
            c.add(Calendar.DATE, 1);

        return c.getTime();
    }


    /**
     * Gets the previous occurence of the given day of the week relative to
     * today. Answers the question: What was the date of last Wednesday?
     *
     * @param   day  Calendar.MONDAY, TUE, ...
     * @return  Previous occuring date of the given day
     */
    public static Date getPreviousDay(int day)
    {
        return getPreviousDay(getToday(), day);
    }


    /**
     * Gets the previous occurrence of the given day 
     * of the week startinf from the the given date.
     *
     * @param    fromDate Date of where to start looking
     * @param    day      Calendar.MONDAY, TUE, ...
     * @return   Previous occurrence of the day
     */
    public static Date getPreviousDay(Date fromDate, int day)
    {
        Date copy = copy(fromDate);
        
        Calendar c = Calendar.getInstance();
        c.setTime(copy);

        // If the search day happens to be the same as day as fromDate, 
        // inch backward a day so we'll find the previous occurrence.        
        if (c.get(Calendar.DAY_OF_WEEK) == day)
            c.add(Calendar.DATE, -1);

        while (c.get(Calendar.DAY_OF_WEEK) != day)
            c.add(Calendar.DATE, -1);
        
        return c.getTime();
    }


    /**
     * Computes the number of days difference between two dates
     *
     * @param    before   The lessor of the two dates
     * @param    after    The greater of the two dates
     * @return   Number of days in between
     */
    public static int getDifferenceInDays(final Date before, final Date after)
    {
        if (equals(before, after))
            return 0;

        Assert.isTrue(before.before(after),
            "Before date " + before + " must be less than after date " + after);

        long secs = (after.getTime() - before.getTime()) / 1000;
        int secsInDay = 60 * 60 * 24;
        return (int) secs / secsInDay;
    }


    /**
     * Computes number of whole weeks difference between two dates
     *
     * @param    before    The lessor of the two dates
     * @param    after     The greater of the two dates
     * @return   Number of whole weeks in between
     */
    public static int getDifferenceInWeeks(Date before, Date after)
    {
        int days = getDifferenceInDays(before, after);
        return (days + 1) / 7;
    }


    /**
     * Determines if a date is before another date.
     * Time is not recognized in this comparison.
     * 
     * @param  isThis      Is this date..
     * @param  beforeThis  Before this date?
     * @return True if isThis is before beforeThis, false otherwise.
     */
    public static boolean isBefore(final Date isThis, final Date beforeThis)
    {
        if (equals(isThis, beforeThis))
            return false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(isThis);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(beforeThis);

        // Compare years
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
            return true;
        else if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
            return false;
        else
        {
            // Years are the same so compare months
            if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH))
                return true;
            else if (cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH))
                return false;
            else
            {
                // Months are the same so compare days
                if (cal1.get(Calendar.DAY_OF_MONTH) < 
                    cal2.get(Calendar.DAY_OF_MONTH))
                    return true;
                else
                    return false;
            }
        }
    }


    /**
     * Determines if a date is between a given date range inclusize of the
     * minimum and maximum dates in the range
     * 
     * @param  date   Date to check
     * @param  begin  Start of range
     * @param  end    End of range
     * @return True if the date is in the range (inclusive)
     */
    public static boolean isBetween(Date date, Date begin, Date end)
    {
        return isOnOrAfter(date, begin) && isOnOrBefore(date, end);
    }


    /**
     * Determines if a date is on or after a given date.
     * 
     * @param   a   Is this date on or after...
     * @param   b   this date?
     * @return  True if a is on or after b, false otherwise
     */
    public static boolean isOnOrAfter(final Date a, final Date b)
    {
        if (equals(a, b))
            return true;
        else
            return isBefore(b, a);
    }


    /**
     * Determines if a date is on or before a given date
     * 
     * @param  isThis      Is this date on or before...
     * @param  beforeThis  this date?
     * @return True if a is on or before b, false otherwise
     */
    public static boolean isOnOrBefore(final Date isThis, final Date beforeThis)
    {
        if (equals(isThis, beforeThis))
            return true;
        else
            return isBefore(isThis, beforeThis);
    }


    /**
     * Zeros out the time portion of the given date
     * Returns original date for chaining
     * 
     * @return Date with zeroed out time
     */
    public static Date zeroTime(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        d.setTime(c.getTime().getTime());
        return d;
    }


    /**
     *  Rounds number of days to a weekly number. If number of
     *  days is not wholly divisible by a week, then the week
     *  is rounded down if less than or equals to the 3rd day
     *  of the week. If greater than or equal to the 4th day
     *  of the week, then it is rounded up.
     *
     *  @param    days   Number of days
     *  @return   Number of weeks in given number of days
     */
    public static int roundToWeeks(int days)
    {
        return Math.round((days / (float) 7));
    }


    /**
     * Subtracts days from a given date
     * 
     * @param  d    Date to subtract days from
     * @param  days Number of days to subtract
     * @return Modified original date for chaining  
     */
    public static Date subtractDays(Date d, int days)
    {
        return addDays(d, -days);
    }
}