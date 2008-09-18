package toolbox.util;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.DateOnlyUtil}.
 */
public class DateOnlyUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(DateOnlyUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    public static void main(String[] args)
    {
        TestRunner.run(DateOnlyUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    public void testGetDifferenceInDays()
    {
        logger_.info("Running testGetDifferenceInDays...");
        
        assertEquals("Difference should be zero days", 0, DateOnlyUtil.getDifferenceInDays(new Date(), new Date()));

        // Same date
        assertEquals("Difference should be zero days", 0, DateOnlyUtil.getDifferenceInDays(new Date(), new Date()));
        
        // Diff is 1 ms
        assertEquals("Difference should be zero days", 0, DateOnlyUtil.getDifferenceInDays(new Date(9999), new Date(99998)));
        
        // Diff is 1 sec
        Date now = new Date();
        assertEquals("Difference should be zero days", 0, DateOnlyUtil.getDifferenceInDays(now, new Date(now.getTime() + 1000)));

        // Diff is 1 minute
        assertEquals("Difference should be zero days", 0, DateOnlyUtil.getDifferenceInDays(now, new Date(now.getTime() + 60000)));
        
        now = new Date();
        Date then = DateOnlyUtil.addDays((Date) now.clone(), 1);
        assertEquals("Difference should be 1 days", 1, DateOnlyUtil.getDifferenceInDays(now, then));

        now = new Date();
        then = DateOnlyUtil.addWeeks((Date) now.clone(), 1);
        assertEquals("Difference should be 7 days", 7, DateOnlyUtil.getDifferenceInDays(now, then));

        now = new Date();
        then = DateOnlyUtil.addWeeks((Date) now.clone(), 4);
        assertEquals("Difference should be 28 days", 28, DateOnlyUtil.getDifferenceInDays(now, then));
    }

    public void testGetDifferenceInWeeks_Zero()
    {
        logger_.info("Running testGetDifferenceInWeeks_Zero...");
        assertEquals("Difference should be zero weeks", 0, DateOnlyUtil.getDifferenceInWeeks(new Date(), new Date()));
    }

    public void testGetDifferenceInWeeks_One()
    {
        logger_.info("Running testGetDifferenceInWeeks_One...");
        Date now = new Date();
        Date then = DateOnlyUtil.addWeeks((Date) now.clone(), 1);
        assertEquals("Difference should be 1 week", 1, DateOnlyUtil.getDifferenceInWeeks(now, then));
    }

    public void testGetDifferenceInWeeks_Many()
    {
        logger_.info("Running testGetDifferenceInWeeks_Many...");
        Date now = new Date();
        Date then = DateOnlyUtil.addWeeks((Date) now.clone(), 7);
        assertEquals("Difference should be 7 weeks", 7, DateOnlyUtil.getDifferenceInWeeks(now, then));
    }

    public void testIsBefore() throws Exception
    {
        logger_.info("Running testIsBefore...");
        
        // Daily difference
        assertTrue("Before same date should be false", !DateOnlyUtil.isBefore(new Date(), new Date()));

        Date now = new Date();
        Date then = DateOnlyUtil.addDays((Date) now.clone(), 1);
        assertTrue("Is before should be true for days", DateOnlyUtil.isBefore(now, then));

        now = new Date();
        then = DateOnlyUtil.subtractDays((Date) now.clone(), 1);
        assertTrue("Is before should be false for days", !DateOnlyUtil.isBefore(now, then));

        // Monthly difference
        now = new Date();
        then = DateOnlyUtil.addWeeks((Date) now.clone(), 8);
        assertTrue("Is before should be true for months", DateOnlyUtil.isBefore(now, then));

        now = new Date();
        then = DateOnlyUtil.subtractDays((Date) now.clone(), 60);
        assertTrue("Is before should be false for months", !DateOnlyUtil.isBefore(now, then));

        // Yearly difference
        now = new Date();
        then = DateOnlyUtil.addWeeks((Date) now.clone(), 60);
        assertTrue("Is before should be true for years", DateOnlyUtil.isBefore(now, then));

        now = new Date();
        then = DateOnlyUtil.subtractDays((Date) now.clone(), 500);
        assertTrue("Is before should be false for years", !DateOnlyUtil.isBefore(now, then));
    }

    public void testIsBetween()
    {
        logger_.info("Running testIsBetween...");
        
        Date beginRange = DateOnlyUtil.addWeeks(DateOnlyUtil.getToday(), -1);
        Date endRange   = DateOnlyUtil.addWeeks(DateOnlyUtil.getToday(), 1);

        Date midRange    = DateOnlyUtil.getToday();
        Date beforeRange = DateOnlyUtil.subtractDays(DateOnlyUtil.copy(beginRange), 1);
        Date afterRange  = DateOnlyUtil.addDays(DateOnlyUtil.copy(endRange), 1);

        Date beginBorder = DateTimeUtil.getStartOfDay(beginRange);
        Date endBorder   = DateTimeUtil.getEndOfDay(endRange);

        assertTrue("Date should be in min range", DateOnlyUtil.isBetween(midRange, beginRange, endRange));
        assertTrue("Date should be before range", !DateOnlyUtil.isBetween(beforeRange, beginRange, endRange));
        assertTrue("Date should be after range", !DateOnlyUtil.isBetween(afterRange, beginRange, endRange));
        assertTrue("Date should be on begin border", DateOnlyUtil.isBetween(beginBorder, beginRange, endRange));
        assertTrue("Date should be on end border", DateOnlyUtil.isBetween(endBorder, beginRange, endRange));
    }

    public void testRoundToWeeks() throws Exception
    {
        logger_.info("Running testRoundToWeeks...");
        assertEquals("Number of weeks in 0 days should be 0", 0, DateOnlyUtil.roundToWeeks(0));
        assertEquals("Number of weeks in 1 days should be 0", 0, DateOnlyUtil.roundToWeeks(1));
        assertEquals("Number of weeks in 3 days should be 0", 0, DateOnlyUtil.roundToWeeks(3));
        assertEquals("Number of weeks in 4 days should be 1", 1, DateOnlyUtil.roundToWeeks(4));
        assertEquals("Number of weeks in 6 days should be 1", 1, DateOnlyUtil.roundToWeeks(6));
        assertEquals("Number of weeks in 7 days should be 1", 1, DateOnlyUtil.roundToWeeks(7));
        assertEquals("Number of weeks in 10 days should be 1", 1, DateOnlyUtil.roundToWeeks(10));
        assertEquals("Number of weeks in 11 days should be 2", 2, DateOnlyUtil.roundToWeeks(11));
        assertEquals("Number of weeks in 14 days should be 2", 2, DateOnlyUtil.roundToWeeks(14));
    }

    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        // Equality
        Date now = new Date();
        assertTrue(DateOnlyUtil.equals(now, now));
 
        // Today != tomorrow
        Date tomorrow = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 1);
        assertTrue(!DateOnlyUtil.equals(now, tomorrow)); 
        
        // Today with time == Today with different time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        Date sameDay = cal.getTime();
        assertTrue(DateOnlyUtil.equals(now, sameDay));
    }
    
    public void testAddDays()
    {
        logger_.info("Running testAddDays...");
        
        Date today = new Date();
        Date tomorrow = DateOnlyUtil.addDays(new Date(), 1);
        DateOnlyUtil.addDays(tomorrow, -1);
        assertTrue(DateOnlyUtil.equals(today, tomorrow));
    }
    
    public void testCompare()
    {
        logger_.info("Running testCompare...");
        
        Date today = new Date();
        Date tomorrow = DateOnlyUtil.addDays(new Date(), 1);
        Date yesterday = DateOnlyUtil.addDays(new Date(), -1);
        
        assertTrue(DateOnlyUtil.compare(today, today) == 0);
        assertTrue(DateOnlyUtil.compare(today, tomorrow) < 0);
        assertTrue(DateOnlyUtil.compare(tomorrow, yesterday) > 0);       
    }
    
    public void testAddWeeks()
    {
        logger_.info("Running testAddWeeks...");
        
        int weeks = 3;
        int days = weeks * 7;
        
        Date futureWeeks = DateOnlyUtil.addWeeks(new Date(), weeks);
        Date futureDays  = DateOnlyUtil.addDays(new Date(), days);
        
        assertTrue(DateOnlyUtil.equals(futureWeeks, futureDays));
    }
 
    public void testCopy()
    {
        logger_.info("Running testCopy...");
        
        Date today = new Date();
        Date copy = DateOnlyUtil.copy(today);
        assertTrue(DateOnlyUtil.equals(today, copy));
    }
    
    public void testIsOnOrBefore()
    {
        logger_.info("Running testIsOnOrBefore...");
        
        Date now       = new Date();
        Date today     = DateOnlyUtil.getToday();
        Date yesterday = DateOnlyUtil.subtractDays(DateOnlyUtil.getToday(), 1);
        Date tomorrow  = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 1);
        Date wayBefore = DateOnlyUtil.subtractDays(DateOnlyUtil.getToday(), 100);
        Date wayAfter  = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 100);
        
        assertTrue(DateOnlyUtil.isOnOrBefore(today, now));
        assertTrue(DateOnlyUtil.isOnOrBefore(yesterday, now));        
        assertTrue(!DateOnlyUtil.isOnOrBefore(tomorrow, now));
        assertTrue(DateOnlyUtil.isOnOrBefore(wayBefore, now));
        assertTrue(!DateOnlyUtil.isOnOrBefore(wayAfter, now));
    }

    public void testIsOnOrAfter()
    {
        logger_.info("Running testIsOnOrAfter...");
        
        Date now       = new Date();
        Date today     = DateOnlyUtil.getToday();
        Date yesterday = DateOnlyUtil.subtractDays(DateOnlyUtil.getToday(), 1);
        Date tomorrow  = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 1);
        Date wayBefore = DateOnlyUtil.subtractDays(DateOnlyUtil.getToday(), 100);
        Date wayAfter  = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 100);
        
        assertTrue(DateOnlyUtil.isOnOrAfter(today, now));
        assertTrue(!DateOnlyUtil.isOnOrAfter(yesterday, now));        
        assertTrue(DateOnlyUtil.isOnOrAfter(tomorrow, now));
        assertTrue(!DateOnlyUtil.isOnOrAfter(wayBefore, now));
        assertTrue(DateOnlyUtil.isOnOrAfter(wayAfter, now));
    }
    
    public void testGetToday()
    {
        logger_.info("Running testGetToday...");
        
        Date today = DateOnlyUtil.getToday();
        Date now   = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(cal.get(Calendar.MINUTE), 0);
        assertEquals(cal.get(Calendar.SECOND), 0);
        assertEquals(cal.get(Calendar.MILLISECOND), 0);
        
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(now);
        
        assertEquals(cal.get(Calendar.YEAR), calNow.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), calNow.get(Calendar.MONTH));
        assertEquals(cal.get(Calendar.DATE), calNow.get(Calendar.DATE));        
    }
    
    public void testGetBeginningOfMonth()
    {
        logger_.info("Running testGetBeginningOfMonth...");
        
        // Beginning of month
        Date bom = DateOnlyUtil.getBeginningOfMonth();        
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
    
    public void testGetNextDay()
    {
        logger_.info("Running testGetNextDay...");   
        
        Date today = DateOnlyUtil.getToday();
        Calendar ctoday = Calendar.getInstance();
        ctoday.setTime(today);
        int dayToday = ctoday.get(Calendar.DAY_OF_WEEK);
        
        Date tomorrow = DateOnlyUtil.addDays(DateOnlyUtil.getToday(), 1);
        Calendar ctomorrow = Calendar.getInstance();
        ctomorrow.setTime(tomorrow);
        int dayTomorrow = ctomorrow.get(Calendar.DAY_OF_WEEK);

        Date nextDay = DateOnlyUtil.getNextDay(dayTomorrow);        
        Calendar cnextday = Calendar.getInstance();
        cnextday.setTime(nextDay);
        
        // If today is monday, check for next tuesday. Should be tomorrow!
        assertEquals(dayTomorrow, cnextday.get(Calendar.DAY_OF_WEEK)); 
        
        // If today is monday, check for next monday, should be a week from now!
        assertTrue(DateOnlyUtil.equals(DateOnlyUtil.addWeeks(DateOnlyUtil.getToday(), 1), DateOnlyUtil.getNextDay(dayToday)));
    }
    
    public void testPreviousDay()
    {
        logger_.info("Running testGetPreviousDay...");   
        
        Date today = DateOnlyUtil.getToday();
        Calendar ctoday = Calendar.getInstance();
        ctoday.setTime(today);
        int dayToday = ctoday.get(Calendar.DAY_OF_WEEK);
        
        Date yesterday = DateOnlyUtil.subtractDays(DateOnlyUtil.getToday(), 1);
        Calendar cyesterday = Calendar.getInstance();
        cyesterday.setTime(yesterday);
        int dayYesterday = cyesterday.get(Calendar.DAY_OF_WEEK);

        Date previousDay = DateOnlyUtil.getPreviousDay(dayYesterday);        
        Calendar cpreviousday = Calendar.getInstance();
        cpreviousday.setTime(previousDay);
        
        // If today is monday, check for previous sunday. Should be yesterday!
        assertEquals(dayYesterday, cpreviousday.get(Calendar.DAY_OF_WEEK)); 
        
        // If today is monday, check for previous monday,
        // should be a week in the past!
        assertTrue(DateOnlyUtil.equals(DateOnlyUtil.addWeeks(DateOnlyUtil.getToday(), -1), DateOnlyUtil.getPreviousDay(dayToday)));
    }
    
    public void testSubstractDays()
    {
        logger_.info("Running testSubstractDays...");
        
        Date today = new Date();
        Date yesterday = DateOnlyUtil.addDays(new Date(), 1);
        DateOnlyUtil.subtractDays(today, -1);
        assertTrue(DateOnlyUtil.equals(today, yesterday));
    }
    
    public void testZeroTime()
    {
        logger_.info("Running testZeroTime...");
        
        Date now = new Date();
        Date zeroed = DateOnlyUtil.zeroTime(now);

        Calendar c = Calendar.getInstance();
        c.setTime(zeroed);
                
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }    
    
    public void testFormat()
    {
        logger_.info("Running testFormat...");
        logger_.debug("Formatted date: " + DateOnlyUtil.format(new Date()));       
    }
}