package toolbox.util;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;

/**
 * Unit test for {@link toolbox.util.DateTimeUtil}.
 */
public class DateTimeUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(DateTimeUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    public static void main(String[] args) {
		TestRunner.run(DateTimeUtilTest.class);
	}

    // --------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    public void testAdd() {
        logger_.info("Running testAdd...");
        
        Date d = DateTimeUtil.getStartOfDay();
        logger_.debug("Before adding: " + DateTimeUtil.format(d));
        
        DateTimeUtil.add(d, 1, 1, 1, 1, 1, 1);
        logger_.debug("After adding: " + DateTimeUtil.format(d));      
    }
    
    
    public void testGetStartOfTime() {
        logger_.info("Running testGetBeginningOfTime...");

        Date d = DateTimeUtil.getStartOfTime();
        logger_.debug("Beginning of time: " + DateTimeUtil.format(d));
    }

    
    public void testGetEndOfTime() {
        logger_.info("Running testGetEndOfTime...");

        Date d = DateTimeUtil.getEndOfTime();
        logger_.debug("End of time: " + DateTimeUtil.format(d));
    }

    
    public void testFormatToSecond() {
        logger_.info("Running testFormatToSecond...");

        Date d = new Date();
        logger_.debug("Formatted to seconds: " + DateTimeUtil.formatToSecond(d));
    }
    
    
    public void testGetStartOfDay() throws Exception {
		logger_.info("Running testGetStartOfDay...");

		Date now = new Date();
		Date start = DateTimeUtil.getStartOfDay();
		logger_.debug("Start of day: " + start);

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);

		// There is the possibility that if the first two lines of this test run 
		// on the end of day boundary (23:59:59 999) that this test will fail.
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);

		assertEquals(nowCal.get(Calendar.YEAR), startCal.get(Calendar.YEAR));
		assertEquals(nowCal.get(Calendar.MONTH), startCal.get(Calendar.MONTH));
		assertEquals(nowCal.get(Calendar.DATE), startCal.get(Calendar.DATE));
		assertEquals(0, startCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, startCal.get(Calendar.MINUTE));
		assertEquals(0, startCal.get(Calendar.SECOND));
		assertEquals(0, startCal.get(Calendar.MILLISECOND));
	}

	public void testGetStartOfDay_Date() throws Exception {
		logger_.info("Running testGetStartOfDay_Date...");

		Date now = new Date();
		Date start = DateTimeUtil.getStartOfDay(now);
		logger_.debug("Start of day: " + start);

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);

		assertEquals(nowCal.get(Calendar.YEAR), startCal.get(Calendar.YEAR));
		assertEquals(nowCal.get(Calendar.MONTH), startCal.get(Calendar.MONTH));
		assertEquals(nowCal.get(Calendar.DATE), startCal.get(Calendar.DATE));
		assertEquals(0, startCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, startCal.get(Calendar.MINUTE));
		assertEquals(0, startCal.get(Calendar.SECOND));
		assertEquals(0, startCal.get(Calendar.MILLISECOND));
	}

	public void testGetStartOfDay_Date_AlreadyAtStart() throws Exception {
		logger_.info("Running testGetStartOfDay_Date_AlreadyAtStart...");

		Date now = new Date();
		Date start = DateTimeUtil.getStartOfDay(now);
		Date startAgain = DateTimeUtil.getStartOfDay(start);
		assertEquals(start, startAgain);
	}

	public void testGetEndOfDay_Date() {
		logger_.info("Running testGetEndOfDay_Date...");

		Date now = new Date();
		Date end = DateTimeUtil.getEndOfDay(now);
		logger_.debug("End of day: " + end);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);

		assertEquals(nowCal.get(Calendar.YEAR), endCal.get(Calendar.YEAR));
		assertEquals(nowCal.get(Calendar.MONTH), endCal.get(Calendar.MONTH));
		assertEquals(nowCal.get(Calendar.DATE), endCal.get(Calendar.DATE));
		assertEquals(23, endCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, endCal.get(Calendar.MINUTE));
		assertEquals(59, endCal.get(Calendar.SECOND));
		assertEquals(999, endCal.get(Calendar.MILLISECOND));
	}

	public void testGetEndOfDay() {
		logger_.info("Running testGetEndOfDay...");

		Date now = new Date();
		Date end = DateTimeUtil.getEndOfDay();
		logger_.debug("End of day: " + end);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);

		// There is the possibility that if the first two lines of this test run 
		// on the end of day boundary (23:59:59 999) that this test will fail.
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);

		assertEquals(nowCal.get(Calendar.YEAR), endCal.get(Calendar.YEAR));
		assertEquals(nowCal.get(Calendar.MONTH), endCal.get(Calendar.MONTH));
		assertEquals(nowCal.get(Calendar.DATE), endCal.get(Calendar.DATE));
		assertEquals(23, endCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, endCal.get(Calendar.MINUTE));
		assertEquals(59, endCal.get(Calendar.SECOND));
		assertEquals(999, endCal.get(Calendar.MILLISECOND));
	}

	public void testGetEndOfDay_Date_AlreadyAtEnd() throws Exception {
		logger_.info("Running testGetEndOfDay_Date_AlreadyAtEnd...");

		Date now = new Date();
		Date end = DateTimeUtil.getEndOfDay(now);
		Date endAgain = DateTimeUtil.getEndOfDay(end);
		assertEquals(end, endAgain);
	}    
}