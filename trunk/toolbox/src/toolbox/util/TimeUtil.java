package toolbox.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that deals only with the TIME portions of the 
 * <code>java.util.Date</code> object.
 */
public final class TimeUtil
{
    // Clover private constructor workaround
    static { new TimeUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Private constructor
     */
    private TimeUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Formats the time portion of a Date object.
     * 
     * @param d Date with time to format
     * @return Time in dashed hh:mma format
     */
    public static String format(Date d)
    {
        DateFormat df = new SimpleDateFormat("hh:mma");
        return df.format(d);
    }
}
