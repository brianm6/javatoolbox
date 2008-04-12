package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that deals only with the TIME portions of the 
 * <code>java.util.Date</code> object.
 * 
 * @see toolbox.util.DateTimeUtil
 */
public final class TimeUtil
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static singlegon.
     */
    private TimeUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Formats the time portion of a Date in hh:mma format. 
     * 
     * @param d Date containing the time to format.
     * @return String
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("hh:mma");
        return df.format(d);
    }
}
