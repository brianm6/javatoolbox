package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.AssertionException;
import toolbox.util.DateTimeUtil;
import toolbox.util.DateUtil;

/**
 * DateUtil test class
 */
public class DateUtilTest extends TestCase
{
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


    /**
     * Tests getDifferenceInDays()
     */
    public void testGetDifferenceInDays()
    {
        assertEquals(
            "Difference should be zero days",
            0,
            DateUtil.getDifferenceInDays(new Date(), new Date()));

        {
            Date now = new Date();
            Date then = DateUtil.addDays((Date)now.clone(), 1);
            assertEquals(
                "Differense should be 1 days",
                1,
                DateUtil.getDifferenceInDays(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 1);
            assertEquals(
                "Differense should be 7 days",
                7,
                DateUtil.getDifferenceInDays(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 4);
            assertEquals(
                "Differense should be 28 days",
                28,
                DateUtil.getDifferenceInDays(now, then));
        }


        try
        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 4);
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

        assertEquals(
            "Difference should be zero weeks",
            0,
            DateUtil.getDifferenceInWeeks(new Date(), new Date()));

        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 1);
            assertEquals(
                "Differense should be 1 weeks",
                1,
                DateUtil.getDifferenceInWeeks(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 7);
            assertEquals(
                "Differense should be 7 weeks",
                7,
                DateUtil.getDifferenceInWeeks(now, then));
        }

        {
            Calendar cc = Calendar.getInstance();
            cc.set(2001, 4 - 1, 23);
            Date now = cc.getTime();
            cc.set(2001, 5 - 1, 20);
            Date then = cc.getTime();
            assertEquals(
                "Differense should be 4 weeks",
                4,
                DateUtil.getDifferenceInWeeks(now, then));
        }
    }


    /**
     * Tests isBefore()
     */
    public void testIsBefore() throws Exception
    {

        /** daily difference **/
        assertTrue(
            "Before same date should be false",
            !DateUtil.isBefore(new Date(), new Date()));

        {
            Date now = new Date();
            Date then = DateUtil.addDays((Date)now.clone(), 1);
            assertTrue(
                "Is before should be true for days",
                DateUtil.isBefore(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.subtractDays((Date)now.clone(), 1);
            assertTrue(
                "Is before should be false for days",
                !DateUtil.isBefore(now, then));
        }

        /** monthly difference **/
        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 8);
            assertTrue(
                "Is before should be true for months",
                DateUtil.isBefore(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.subtractDays((Date)now.clone(), 60);
            assertTrue(
                "Is before should be false for months",
                !DateUtil.isBefore(now, then));
        }

        /** yearly differecnce **/
        {
            Date now = new Date();
            Date then = DateUtil.addWeeks((Date)now.clone(), 60);
            assertTrue(
                "Is before should be true for years",
                DateUtil.isBefore(now, then));
        }

        {
            Date now = new Date();
            Date then = DateUtil.subtractDays((Date)now.clone(), 500);
            assertTrue(
                "Is before should be false for years",
                !DateUtil.isBefore(now, then));
        }
    }

    /**
     * Tests isBetween()
     */
    public void testIsBetween()
    {

        Date beginRange = DateUtil.addWeeks(DateUtil.today(),-1);
        Date endRange   = DateUtil.addWeeks(DateUtil.today(), 1);

        Date midRange    = DateUtil.today();
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
            1,
            DateUtil.roundToWeeks(7));
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

    }
}