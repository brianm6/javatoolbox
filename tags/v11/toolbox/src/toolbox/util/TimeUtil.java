package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that deals only with the TIME portions of the 
 * <code>java.util.Date</code> object
 */
public class TimeUtil
{
    /**
     * Private constructor
     */
    private TimeUtil()
    {
    }

    /**
     * @return  Date in dashed MM-dd-yyyy format
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("hh:mma");
        return df.format(d);
    }
}
