package toolbox.util.test;

import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.RandomUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for ElapsedTime
 */
public class ElapsedTimeTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(ElapsedTimeTest.class);
       
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(ElapsedTimeTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ElapsedTimeTest.
     * 
     * @param arg0  Name
     */
    public ElapsedTimeTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests getDifference() for a ms
     */
    public void testMillisecondElapsed()
    {
        logger_.info("Running testMillisecondElapsed...");
        
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
        logger_.info("Running testSecondElapsed...");
        
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
        logger_.info("Running testMinuteElapsed...");
        
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
        logger_.info("Running testHourElapsed...");
        
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
        logger_.info("Running testDayElapsed...");
        
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
        logger_.info("Running testMillisRollover...");
        
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
        logger_.info("Running testSecondsRollover...");
        
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
        logger_.info("Running testMinutesRollover...");
        
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
        logger_.info("Running testHoursRollover...");
        
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
        logger_.info("Running testEquals...");
        
        ElapsedTime time    = new ElapsedTime(1,2,3,4,5);
        ElapsedTime compare = new ElapsedTime(1,2,3,4,5);
        assertTrue("times don't match", time.equals(compare));
    }


    /**
     * Tests equals() for not equal
     */
    public void testEqualsNot()    
    {
        logger_.info("Running testEqualsNot...");
        
        ElapsedTime time    = new ElapsedTime(5,4,3,2,1);
        ElapsedTime compare = new ElapsedTime(1,2,3,4,5);
        assertTrue("times should not match", !time.equals(compare));        
    }
    
    
    /**
     * Tests constructor 1
     */
    public void testConsturctor1()
    {
        logger_.info("Running testConsturctor1...");
        
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
        logger_.info("Running testCopyConstructor...");
        
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
        logger_.info("Running testConsturctor3...");
        
        Date start = new Date();
        ThreadUtil.sleep(RandomUtil.nextInt(1000));
        Date end = new Date();   
        
        ElapsedTime time = new ElapsedTime(start.getTime(), end.getTime());
        logger_.info(time);
    }
}