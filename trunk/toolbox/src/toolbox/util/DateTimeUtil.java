package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that deals only with the DATE portions of the 
 * <code>java.util.Date</code> object
 * 
 *        
 * 
 */
public class DateTimeUtil
{
    /**
     * Constructor for DateTimeUtil.
     */
    public DateTimeUtil()
    {
        super();
    }

    /**
     * @return  Date/time in dashed MM-dd-yyyy format
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        return df.format(d);
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