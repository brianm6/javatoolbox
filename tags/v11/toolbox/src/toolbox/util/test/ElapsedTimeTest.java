package toolbox.util.test;

import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;

import toolbox.util.ElapsedTime;
import toolbox.util.RandomUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for ElapsedTime
 */
public class ElapsedTimeTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(ElapsedTimeTest.class);
       
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(ElapsedTimeTest.class);
    }

    
    /**
     * Constructor for ElapsedTimeTest.
     * 
     * @param arg0  Name
     */
    public ElapsedTimeTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests getDifference() for a ms
     */
    public void testMillisecondElapsed()
    {
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.MILLI);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0,0,0,0,1);
        assertEquals("millis is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Tests an elapsed time of one second
     */
    public void testSecondElapsed()
    {
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.SECOND);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0,0,0,1,0);
        assertEquals("seconds is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Tests an elapsed time of one minute
     */
    public void testMinuteElapsed()
    {
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.MINUTE);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0,0,1,0,0);
        assertEquals("minutes is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Tests an elapsed time of one hour
     */
    public void testHourElapsed()
    {
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.HOUR);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0,1,0,0,0);
        assertEquals("hours is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Tests an elapsed time of one day
     */
    public void testDayElapsed()
    {
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.DAY);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(1,0,0,0,0);
        assertEquals("days is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Tests millisecond rollover: 1000ms = 1 sec
     */
    public void testMillisRollover()
    {   
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.SECOND);
        ElapsedTime compare = new ElapsedTime(0,0,0,1,0);
        assertEquals("seconds is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }
        
        
    /**
     * Tests seconds rollover: 60s = 1 minute
     */
    public void testSecondsRollover()
    {   
        ElapsedTime elapsed = new ElapsedTime(0,ElapsedTime.MINUTE);
        ElapsedTime compare = new ElapsedTime(0,0,1,0,0);
        assertEquals("minutes is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }

 
    /**
     * Tests minutes rollover: 60mins = 1 hour
     */
    public void testMinutesRollover()
    {   
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.HOUR);
        ElapsedTime compare = new ElapsedTime(0,1,0,0,0);
        assertEquals("hours is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }


    /**
     * Test hours rollover: 24hrs = 1 day
     */
    public void testHoursRollover()
    {   
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.DAY);
        ElapsedTime compare = new ElapsedTime(1,0,0,0,0);
        assertEquals("days is incorrect", compare, elapsed);
        logger_.info(elapsed);
    }

    
    /**
     * Test equals()
     */
    public void testEquals()
    {
        ElapsedTime time    = new ElapsedTime(1,2,3,4,5);
        ElapsedTime compare = new ElapsedTime(1,2,3,4,5);
        assertTrue("times don't match", time.equals(compare));
    }


    /**
     * Tests equals() for not equal
     */
    public void testEqualsNot()    
    {
        ElapsedTime time    = new ElapsedTime(5,4,3,2,1);
        ElapsedTime compare = new ElapsedTime(1,2,3,4,5);
        assertTrue("times should not match", !time.equals(compare));        
    }
    
    
    /**
     * Tests constructor 1
     */
    public void testConsturctor1()
    {
        Date start = new Date();
        ThreadUtil.sleep(RandomUtil.nextInt(1000));
        Date end = new Date();   
        
        ElapsedTime time = new ElapsedTime(start, end);
        logger_.info(time);
    }
 
     
    /**
     * Tests constructor 2 - copy constructor
     */
    public void testCopyConstructor()
    {       
        Date start = new Date();
        ThreadUtil.sleep(RandomUtil.nextInt(1000));
        Date end = new Date();   
 
        ElapsedTime time = new ElapsedTime(start, end);   
        ElapsedTime copy = new ElapsedTime(time);
        
        assertTrue("times should be equals", time.equals(copy));
        logger_.info(time);
    }
    
    
    /**
     * Tests constructor 3
     */
    public void testConsturctor3()
    {
        Date start = new Date();
        ThreadUtil.sleep(RandomUtil.nextInt(1000));
        Date end = new Date();   
        
        ElapsedTime time = new ElapsedTime(start.getTime(), end.getTime());
        logger_.info(time);
    }
    
}