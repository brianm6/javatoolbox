package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that deals only with the DATE portions of the 
 * <code>java.util.Date</code> object
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
}