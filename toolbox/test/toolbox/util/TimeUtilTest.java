package toolbox.util;

import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.TimeUtil;

/**
 * Unit test for {@link toolbox.util.TimeUtil}.
 */
public class TimeUtilTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(TimeUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(TimeUtilTest.class);
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
        logger_.debug("Formatted time: " + time);
    }
}