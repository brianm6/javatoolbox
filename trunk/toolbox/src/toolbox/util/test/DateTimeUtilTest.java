package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;

/**
 * Unit test for DateTimeUtil
 */
public class DateTimeUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ =
        Logger.getLogger(DateTimeUtilTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(DateTimeUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for DateTimeUtilTest.
     * 
     * @param arg0
     */
    public DateTimeUtilTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests getBeginningOfDay()
     */
    public void testGetBeginningOfDay() throws Exception
    {
        logger_.info("Running testGetBeginningOfDay...");
        
        Date d = DateTimeUtil.getBeginningOfDay();

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        assertTrue("hour not zero", c.get(Calendar.HOUR_OF_DAY) == 0);
        assertTrue("minute not zero", c.get(Calendar.MINUTE) == 0);
        assertTrue("sedond not zero", c.get(Calendar.SECOND) == 0);
        assertTrue("millis not zero", c.get(Calendar.MILLISECOND) == 0);
    }

}