package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;
import toolbox.util.TimeUtil;

/**
 * Unit test for TimeUtil
 */
public class TimeUtilTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ =
        Logger.getLogger(TimeUtilTest.class);
        
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(TimeUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for DateTimeUtilTest.
     * 
     * @param arg0  Name
     */
    public TimeUtilTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests format()
     */
    public void testFormat()
    {
        logger_.info("Running testFormat...");
        Date d = new Date();
        String time = TimeUtil.format(d);
        logger_.info("Formatted time: " + time);
    }
}