package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.AssertionException;
import toolbox.util.DateTimeUtil;
import toolbox.util.DateUtil;

/**
 * Unit test for DateUtil
 */
public class DateUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DateUtilTest.class);
    
    /**
     * Entrypoint
     *
     * @param args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(DateUtilTest.class);
    }

    /**
     * DateUtilTest constructor
     * 
     * @param aName String
     */
    public DateUtilTest(String aName)
    {
        super(aName);
    }


    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests getDifferenceInDays()
     */
    public void testGetDifferenceInDays()
    {
        logger_.info("Testing getDifferenceInDays()");
        
        assertEquals("Difference should be zero days",
            0, DateUtil.getDifferenceInDays(new Date(), new Date()));

        Date now = new Date();
        Date then = DateUtil.addDays((Date)now.clone(), 1);
        assertEquals("Differense should be 1 days",
            1, DateUtil.getDifferenceInDays(now, then));

        now = new Date();
        then = DateUtil.addWeeks((Date)now.clone(), 1);
        assertEquals("Differense should be 7 days",
            7, DateUtil.getDifferenceInDays(now, then));

        now = new Date();
        then = DateUtil.addWeeks((Date)now.clone(), 4);
        assertEquals("Differense should be 28 days",
            28, DateUtil.getDifferenceInDays(now, then));

        try
        {
            now = new Date();
            then = DateUtil.addWeeks((Date)now.clone(), 4);
            DateUtil.getDifferenceInDays(then, now);
            fail("should have failed for dates out of order");
        }
        catch (AssertionException e)
        {
            assertTrue(true);
        }
    }


    /**
     * Tests getDifferenceInWeeks()
     */
    public void testGetDifferenceInWeeks()
    {
        logger_.info("Testing getDifferenceInWeeks()");
        
        assertEquals("Difference should be zero weeks",
            0, DateUtil.getDifferenceInWeeks(new Date(), new Date()));
        
        Date now = new Date();
        Date then = DateUtil.addWeeks((Date)now.clone(), 1);
        assertEquals("Differense should be 1 weeks",
            1, DateUtil.getDifferenceInWeeks(now, then));

        now = new Date();
        then = DateUtil.addWeeks((Date)now.clone(), 7);
        assertEquals("Differense should be 7 weeks", 7,
            DateUtil.getDifferenceInWeeks(now, then));

        Calendar cc = Calendar.getInstance();
        cc.set(2001, 4 - 1, 23);
        now = cc.getTime();
        cc.set(2001, 5 - 1, 20);
        then = cc.getTime();
        assertEquals("Differense should be 4 weeks",
            4, DateUtil.getDifferenceInWeeks(now, then));
    }


    /**
     * Tests isBefore()
     */
    public void testIsBefore() throws Exception
    {
        logger_.info("Testing isBefore()");
        
        // Daily difference
        assertTrue("Before same date should be false",
            !DateUtil.isBefore(new Date(), new Date()));

        Date now = new Date();
        Date then = DateUtil.addDays((Date)now.clone(), 1);
        assertTrue("Is before should be true for days", 
            DateUtil.isBefore(now, then));

        now = new Date();
        then = DateUtil.subtractDays((Date)now.clone(), 1);
        assertTrue("Is before should be false for days",
            !DateUtil.isBefore(now, then));

        // Monthly difference
        now = new Date();
        then = DateUtil.addWeeks((Date)now.clone(), 8);
        assertTrue("Is before should be true for months",
            DateUtil.isBefore(now, then));

        now = new Date();
        then = DateUtil.subtractDays((Date)now.clone(), 60);
        assertTrue("Is before should be false for months",
            !DateUtil.isBefore(now, then));

        // Yearly difference
        now = new Date();
        then = DateUtil.addWeeks((Date)now.clone(), 60);
        assertTrue("Is before should be true for years",
            DateUtil.isBefore(now, then));

        now = new Date();
        then = DateUtil.subtractDays((Date)now.clone(), 500);
        assertTrue("Is before should be false for years",
            !DateUtil.isBefore(now, then));
    }

    /**
     * Tests isBetween()
     */
    public void testIsBetween()
    {
        logger_.info("Testing isBetween()");
        
        Date beginRange = DateUtil.addWeeks(DateUtil.getToday(),-1);
        Date endRange   = DateUtil.addWeeks(DateUtil.getToday(), 1);

        Date midRange    = DateUtil.getToday();
        Date beforeRange = DateUtil.subtractDays(DateUtil.copy(beginRange), 1);
        Date afterRange  = DateUtil.addDays(DateUtil.copy(endRange), 1);

        Date beginBorder = DateTimeUtil.getBeginningOfDay(beginRange);
        Date endBorder   = DateTimeUtil.getEndOfDay(endRange);

        assertTrue(
            "Date should be in min range",
            DateUtil.isBetween(midRange, beginRange, endRange));
        assertTrue(
            "Date should be before range",
            !DateUtil.isBetween(beforeRange, beginRange, endRange));
        assertTrue(
            "Date should be after range",
            !DateUtil.isBetween(afterRange, beginRange, endRange));
        assertTrue(
            "Date should be on begin border",
            DateUtil.isBetween(beginBorder, beginRange, endRange));
        assertTrue(
            "Date should be on end border",
            DateUtil.isBetween(endBorder, beginRange, endRange));

    }

    /**
     * Tests the roundToWeeks() method
     */
    public void testRoundToWeeks() throws Exception
    {
        logger_.info("Testing roundToWeeks()");
        
        assertEquals(
            "Number of weeks in 0 days should be 0",
            0,
            DateUtil.roundToWeeks(0));
        assertEquals(
            "Number of weeks in 1 days should be 0",
            0,
            DateUtil.roundToWeeks(1));
        assertEquals(
            "Number of weeks in 3 days should be 0",
            0,
            DateUtil.roundToWeeks(3));
        assertEquals(
            "Number of weeks in 4 days should be 1",
            1,
            DateUtil.roundToWeeks(4));
        assertEquals(
            "Number of weeks in 6 days should be 1",
            1,
            DateUtil.roundToWeeks(6));
        assertEquals(
            "Number of weeks in 7 days should be 1", 
            1, DateUtil.roundToWeeks(7));
        assertEquals(
            "Number of weeks in 10 days should be 1",
            1,
            DateUtil.roundToWeeks(10));
        assertEquals(
            "Number of weeks in 11 days should be 2",
            2,
            DateUtil.roundToWeeks(11));
        assertEquals(
            "Number of weeks in 14 days should be 2",
            2,
            DateUtil.roundToWeeks(14));

    /**
     * Tests equals()
     */
    public void testEquals()
    {
        logger_.info("Testing equals()");
        
        // Equality
        Date now = new Date();
        assertTrue(DateUtil.equals(now, now));
 
        // Today != tomorrow       
        Date tomorrow = DateUtil.addDays(DateUtil.getToday(),1);
        assertTrue(!DateUtil.equals(now, tomorrow)); 
        
        // Today with time == Today with different time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        Date sameDay = cal.getTime();
        assertTrue(DateUtil.equals(now, sameDay));
    }    }
 
    
    /**
     * Tests addDays() 
     */
    public void testAddDays()
    {
        logger_.info("Testing addDays()");
        
        Date today = new Date();
        Date tomorrow = DateUtil.addDays(new Date(), 1);
        DateUtil.addDays(tomorrow, -1);
        assertTrue(DateUtil.equals(today, tomorrow));
    }
 
    
    /**
     * Tests compare()
     */
    public void testCompare()
    {
        logger_.info("Testing compare()");
        
        Date today = new Date();
        Date tomorrow = DateUtil.addDays(new Date(), 1);
        Date yesterday = DateUtil.addDays(new Date(), -1);
        
        assertTrue(DateUtil.compare(today, today) == 0);
        assertTrue(DateUtil.compare(today, tomorrow) < 0);
        assertTrue(DateUtil.compare(tomorrow, yesterday) > 0);       
    }
    
    
    /**
     * Tests addWeeks()
     */
    public void testAddWeeks()
    {
        logger_.info("Testing addWeeks()");
        
        Date today = new Date();
        int weeks = 3;
        int days = weeks * 7;
        
        Date futureWeeks = DateUtil.addWeeks(new Date(), weeks);
        Date futureDays  = DateUtil.addDays(new Date(), days);
        
        assertTrue(DateUtil.equals(futureWeeks, futureDays));
    }
 
 
    /**
     * Tests copy()   
     */
    public void testCopy()
    {
        logger_.info("Testing copy()");
        
        Date today = new Date();
        Date copy = DateUtil.copy(today);
        assertTrue(DateUtil.equals(today, copy));
    }
    
    
    /**
     * Tests isOnOrBefore()
     */
    public void testIsOnOrBefore()
    {
        logger_.info("Testing isOnOrBefore()");
        
        Date now       = new Date();
        Date today     = DateUtil.getToday();
        Date yesterday = DateUtil.subtractDays(DateUtil.getToday(),1);
        Date tomorrow  = DateUtil.addDays(DateUtil.getToday(), 1);
        Date wayBefore = DateUtil.subtractDays(DateUtil.getToday(), 100);
        Date wayAfter  = DateUtil.addDays(DateUtil.getToday(), 100);
        
        assertTrue(DateUtil.isOnOrBefore(today, now));
        assertTrue(DateUtil.isOnOrBefore(yesterday, now));        
        assertTrue(!DateUtil.isOnOrBefore(tomorrow, now));
        assertTrue(DateUtil.isOnOrBefore(wayBefore, now));
        assertTrue(!DateUtil.isOnOrBefore(wayAfter, now));
    }

    /**
     * Tests isOnOrAfter()
     */
    public void testIsOnOrAfter()
    {
        logger_.info("Testing isOnOrAfter()");
        
        Date now       = new Date();
        Date today     = DateUtil.getToday();
        Date yesterday = DateUtil.subtractDays(DateUtil.getToday(),1);
        Date tomorrow  = DateUtil.addDays(DateUtil.getToday(), 1);
        Date wayBefore = DateUtil.subtractDays(DateUtil.getToday(), 100);
        Date wayAfter  = DateUtil.addDays(DateUtil.getToday(), 100);
        
        assertTrue(DateUtil.isOnOrAfter(today, now));
        assertTrue(!DateUtil.isOnOrAfter(yesterday, now));        
        assertTrue(DateUtil.isOnOrAfter(tomorrow, now));
        assertTrue(!DateUtil.isOnOrAfter(wayBefore, now));
        assertTrue(DateUtil.isOnOrAfter(wayAfter, now));
    }
    
    /**
     * Tests getToday()
     */
    public void testGetToday()
    {
        logger_.info("Testing getToday()");
        
        Date today = DateUtil.getToday();
        Date now   = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        
        assertEquals(cal.get(Calendar.HOUR), 0);
        assertEquals(cal.get(Calendar.MINUTE), 0);
        assertEquals(cal.get(Calendar.SECOND), 0);
        assertEquals(cal.get(Calendar.MILLISECOND), 0);
        
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(now);
        
        assertEquals(cal.get(Calendar.YEAR), calNow.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), calNow.get(Calendar.MONTH));
        assertEquals(cal.get(Calendar.DATE), calNow.get(Calendar.DATE));        
    }
    
    /**
     * Tests getBeginningOfMonth()
     */
    public void testGetBeginningOfMonth()
    {
        logger_.info("Testing getBeginningOfMonth()");
        
        // Beginning of month
        Date bom = DateUtil.getBeginningOfMonth();        
        Calendar cbom = Calendar.getInstance();
        cbom.setTime(bom);

        // Right now
        Date now = new Date();        
        Calendar cnow = Calendar.getInstance();
        cnow.setTime(now); 

        // Compare day, month, year.
        assertEquals(1, cbom.get(Calendar.DAY_OF_MONTH));
        assertEquals(cnow.get(Calendar.MONTH), cbom.get(Calendar.MONTH));
        assertEquals(cnow.get(Calendar.YEAR), cbom.get(Calendar.YEAR));
    }
    
    /**
     * Tests getNextDay()
     */
    public void testGetNextDay()
    {
        logger_.info("Testing getNextDay()");   
        
        Date today = DateUtil.getToday();
        Calendar ctoday = Calendar.getInstance();
        ctoday.setTime(today);
        int dayToday = ctoday.get(Calendar.DAY_OF_WEEK);
        
        Date tomorrow = DateUtil.addDays(DateUtil.getToday(), 1);
        Calendar ctomorrow = Calendar.getInstance();
        ctomorrow.setTime(tomorrow);
        int dayTomorrow = ctomorrow.get(Calendar.DAY_OF_WEEK);

        Date nextDay = DateUtil.getNextDay(dayTomorrow);        
        Calendar cnextday = Calendar.getInstance();
        cnextday.setTime(nextDay);
        
        // If today is monday, check for next tuesday. Should be tomorrow!
        assertEquals(dayTomorrow, cnextday.get(Calendar.DAY_OF_WEEK)); 
        
        // If today is monday, check for next monday, should be a week from now!
        assertTrue(DateUtil.equals(
            DateUtil.addWeeks(DateUtil.getToday(), 1), 
                DateUtil.getNextDay(dayToday)));
    }
    
    /**
     * Tests getPreviousDay()
     */
    public void testPreviousDay()
    {
        logger_.info("Testing getPreviousDay()");   
        
        Date today = DateUtil.getToday();
        Calendar ctoday = Calendar.getInstance();
        ctoday.setTime(today);
        int dayToday = ctoday.get(Calendar.DAY_OF_WEEK);
        
        Date yesterday = DateUtil.subtractDays(DateUtil.getToday(), 1);
        Calendar cyesterday = Calendar.getInstance();
        cyesterday.setTime(yesterday);
        int dayYesterday = cyesterday.get(Calendar.DAY_OF_WEEK);

        Date previousDay = DateUtil.getPreviousDay(dayYesterday);        
        Calendar cpreviousday = Calendar.getInstance();
        cpreviousday.setTime(previousDay);
        
        // If today is monday, check for previous sunday. Should be yesterday!
        assertEquals(dayYesterday, cpreviousday.get(Calendar.DAY_OF_WEEK)); 
        
        // If today is monday, check for previous monday, should be a week in the past!
        assertTrue(DateUtil.equals(
            DateUtil.addWeeks(DateUtil.getToday(), -1), 
                DateUtil.getPreviousDay(dayToday)));
    }
    
    /**
     * Tests substractDays() 
     */
    public void testSubstractDays()
    {
        logger_.info("Testing substractDays()");
        
        Date today = new Date();
        Date yesterday = DateUtil.addDays(new Date(), 1);
        DateUtil.subtractDays(today, -1);
        assertTrue(DateUtil.equals(today,yesterday));
    }
    
    /**
     * Tests zeroTime()
     */
    public void testZeroTime()
    {
        logger_.info("Testing zeroTime()");
        
        Date now = new Date();
        Date zeroed = DateUtil.zeroTime(now);

        Calendar c = Calendar.getInstance();
        c.setTime(zeroed);
                
        assertEquals(0, c.get(Calendar.HOUR));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }    
}