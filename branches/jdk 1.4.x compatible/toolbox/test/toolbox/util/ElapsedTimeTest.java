package toolbox.util;

import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.ElapsedTime).
 */
public class ElapsedTimeTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(ElapsedTimeTest.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Max wait time for unit tests = 1 second.
     */
    private static final int MAX_WAIT = 1000;
    
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
        TestRunner.run(ElapsedTimeTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests getDifference() for a ms.
     */
    public void testMillisecondElapsed()
    {
        logger_.info("Running testMillisecondElapsed...");
        
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.MILLI);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0, 0, 0, 0, 1);
        assertEquals("millis is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Tests an elapsed time of one second.
     */
    public void testSecondElapsed()
    {
        logger_.info("Running testSecondElapsed...");
        
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.SECOND);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0, 0, 0, 1, 0);
        assertEquals("seconds is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Tests an elapsed time of one minute.
     */
    public void testMinuteElapsed()
    {
        logger_.info("Running testMinuteElapsed...");
        
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.MINUTE);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0, 0, 1, 0, 0);
        assertEquals("minutes is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Tests an elapsed time of one hour.
     */
    public void testHourElapsed()
    {
        logger_.info("Running testHourElapsed...");
        
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.HOUR);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(0, 1, 0, 0, 0);
        assertEquals("hours is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Tests an elapsed time of one day.
     */
    public void testDayElapsed()
    {
        logger_.info("Running testDayElapsed...");
        
        Date now = new Date();
        Date then = new Date(now.getTime());
        then.setTime(now.getTime()  + ElapsedTime.DAY);
        
        ElapsedTime elapsed = new ElapsedTime(now, then);
        ElapsedTime compare = new ElapsedTime(1, 0, 0, 0, 0);
        assertEquals("days is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Tests millisecond rollover: 1000ms = 1 sec.
     */
    public void testMillisRollover()
    {   
        logger_.info("Running testMillisRollover...");
        
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.SECOND);
        ElapsedTime compare = new ElapsedTime(0, 0, 0, 1, 0);
        assertEquals("seconds is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }
        
    
    /**
     * Tests seconds rollover: 60s = 1 minute.
     */
    public void testSecondsRollover()
    {   
        logger_.info("Running testSecondsRollover...");
        
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.MINUTE);
        ElapsedTime compare = new ElapsedTime(0, 0, 1, 0, 0);
        assertEquals("minutes is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }
 
    
    /**
     * Tests minutes rollover: 60mins = 1 hour.
     */
    public void testMinutesRollover()
    {   
        logger_.info("Running testMinutesRollover...");
        
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.HOUR);
        ElapsedTime compare = new ElapsedTime(0, 1, 0, 0, 0);
        assertEquals("hours is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }

    
    /**
     * Test hours rollover: 24hrs = 1 day.
     */
    public void testHoursRollover()
    {   
        logger_.info("Running testHoursRollover...");
        
        ElapsedTime elapsed = new ElapsedTime(0, ElapsedTime.DAY);
        ElapsedTime compare = new ElapsedTime(1, 0, 0, 0, 0);
        assertEquals("days is incorrect", compare, elapsed);
        logger_.debug(elapsed);
    }
    
    
    /**
     * Test equals()
     */
    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        ElapsedTime time = new ElapsedTime(1, 2, 3, 4, 5);
        ElapsedTime compare = new ElapsedTime(1, 2, 3, 4, 5);
        assertTrue("times don't match", time.equals(compare));
    }

    
    /**
     * Tests equals() for not equal.
     */
    public void testEqualsNot()    
    {
        logger_.debug("Running testEqualsNot...");
        
        ElapsedTime time = new ElapsedTime(5, 4, 3, 2, 1);
        ElapsedTime compare = new ElapsedTime(1, 2, 3, 4, 5);
        assertTrue("times should not match", !time.equals(compare));        
    }
    

    /**
     * Test hashCode()
     */
    public void testHashCode()
    {
        logger_.info("Running testHashCode...");
        
        ElapsedTime time = new ElapsedTime(1, 2, 3, 4, 5);
        ElapsedTime compare = new ElapsedTime(1, 2, 3, 4, 5);
        assertEquals(time.hashCode(), compare.hashCode());
    }
    
    
    /**
     * Tests equals() failure.
     */
    public void testEqualsFailure()    
    {
        logger_.info("Running testEqualsFailure...");
        
        ElapsedTime time = new ElapsedTime();
        assertFalse(time.equals("Cannot be compared to a string!"));
    }
    
    
    /**
     * Tests constructor 1.
     */
    public void testConstructor1()
    {
        logger_.info("Running testConstructor1...");
        
        Date start = new Date();
        Date end = new Date(start.getTime() + 1000);   
        ElapsedTime time = new ElapsedTime(start, end);
        logger_.debug("Elapsed time = " + time);
        assertEquals(end.getTime() - start.getTime(), time.getTotalMillis());
        assertTrue(time.getStartTime().before(time.getEndTime()));
    }
     
    
    /**
     * Tests constructor 2 - copy constructor.
     */
    public void testCopyConstructor()
    {       
        logger_.info("Running testCopyConstructor...");
        
        Date start = new Date();
        int delta = 1000 + RandomUtils.nextInt(MAX_WAIT);
        ThreadUtil.sleep(delta);
        Date end = new Date();   
 
        ElapsedTime time = new ElapsedTime(start, end);   
        ElapsedTime copy = new ElapsedTime(time);
        
        assertTrue("times should be equals", time.equals(copy));
        logger_.debug("Elapsed time = " + time);
    }
    
    
    /**
     * Tests constructor 3.
     */
    public void testConstructor3()
    {
        logger_.info("Running testConstructor3...");
        
        Date start = new Date();
        int delta = 1000 + RandomUtils.nextInt(MAX_WAIT);
        ThreadUtil.sleep(delta);
        Date end = new Date();   
        
        ElapsedTime time = new ElapsedTime(start.getTime(), end.getTime());
        logger_.debug("Elapsed time = " + time);
        
        time.setStartTime(new Date());
    }
 
    
    public void testGetTotalMillis() throws Exception {
        logger_.info("Running testGetTotalMillis...");
        
        
        long[] times = new long[] {
            ElapsedTime.MILLI,
            ElapsedTime.SECOND,
            ElapsedTime.MINUTE,
            ElapsedTime.HOUR,
            ElapsedTime.DAY
        };
        
        for (int i = 0; i < times.length; i++) {
            
            // Right on boundary
            ElapsedTime t = new ElapsedTime(0, times[i]);
            assertEquals(times[i], t.getTotalMillis());
   
            // One before boundary
            t = new ElapsedTime(0, times[i] - 1);
            assertEquals(times[i] - 1, t.getTotalMillis());
        
            // One after boundary
            t = new ElapsedTime(0, times[i] + 1);
            assertEquals(times[i] + 1, t.getTotalMillis());
        }
    }
}