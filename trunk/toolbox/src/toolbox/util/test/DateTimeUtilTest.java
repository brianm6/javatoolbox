package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;

/**
 * Unit test for DateTimeUtil.
 */
public class DateTimeUtilTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(DateTimeUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(DateTimeUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests getBeginningOfDay()
     * 
     * @throws Exception on error
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

    
    /**
     * Tests add()
     */
    public void testAdd()
    {
        logger_.info("Running testAdd...");
        
        Date d = DateTimeUtil.getBeginningOfDay();
        logger_.info("Before adding: " + DateTimeUtil.format(d));
        
        DateTimeUtil.add(d, 1, 1, 1, 1, 1, 1);
        logger_.info("After adding: " + DateTimeUtil.format(d));      
    }
    
    
    /**
     * Tests getBeginningOfTime()
     */
    public void testGetBeginningOfTime()
    {
       logger_.info("Running testGetBeginningOfTime...");        
       
       Date d = DateTimeUtil.getBeginningOfTime();
       logger_.info("Beginning of time: " + DateTimeUtil.format(d));
    }   
    
    
    /**
     * Test getEndOfDay()
     */
    public void testGetEndOfDay()
    {
       logger_.info("Running testGetEndOfDay...");
       
       Date d = DateTimeUtil.getEndOfDay();
       logger_.info("End of day: " + DateTimeUtil.format(d));
    }
    
    
    /**
     * Tests getEndOfTime()
     */
    public void testGetEndOfTime()
    {
       logger_.info("Running testGetEndOfTime...");        
       
       Date d = DateTimeUtil.getEndOfTime();
       logger_.info("End of time: " + DateTimeUtil.format(d));
    }
    
    
    /**
     * Tests formatToSecond()
     */
    public void testFormatToSecond()
    {
       logger_.info("Running testFormatToSecond...");        
       
       Date d = new Date();
       logger_.info("Formatted to seconds: " + DateTimeUtil.formatToSecond(d));
    }   
}