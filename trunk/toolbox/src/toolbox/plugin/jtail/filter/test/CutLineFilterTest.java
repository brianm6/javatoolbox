package toolbox.jtail.filter.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.jtail.filter.CutLineFilter;
import toolbox.util.StringUtil;

/**
 * Unit test for CutLineFilter.
 */
public class CutLineFilterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(CutLineFilterTest.class);
        
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
        TestRunner.run(CutLineFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests filter()
     */
    public void testFilter()
    {
        logger_.info("Running testFilter...");
        
        CutLineFilter filter = new CutLineFilter();
        filter.setEnabled(true);
        
        // First char
        filter.setCut("1-1");        
        assertEquals("2345", filter.filter("12345"));
        
        // First char + subset
        filter.setCut("1-2");
        assertEquals("345", filter.filter("12345"));
        
        // All
        filter.setCut("1-5");
        assertEquals("", filter.filter("12345"));
        
        // Subset
        filter.setCut("2-4");
        assertEquals("15", filter.filter("12345"));
        
        // Last char
        filter.setCut("5-5");
        assertEquals("1234", filter.filter("12345"));
    
        // Last char + subset
        filter.setCut("3-5");
        assertEquals("12", filter.filter("12345"));
        
        // Out of range
        filter.setCut("20-30");
        assertEquals("12345", filter.filter("12345"));
        
        // Overlapping out of range
        filter.setCut("3-30");
        assertEquals("12", filter.filter("12345"));
        
        logger_.info(filter.toString());
    }
    
    
    /**
     * Tests the filter when disabled.
     */
    public void testFilterDisabled()
    {
        logger_.info("Running testFilterDisabled...");
        
        String s = "12345";
        CutLineFilter filter = new CutLineFilter();
        filter.setEnabled(false);
        filter.setCut("1-1");        
        assertEquals(s, filter.filter(s));
    }
    
    
    /**
     * Tests the filter for null input.
     */
    public void testFilterNull()
    {
        logger_.info("Running testFilterNull...");
        
        CutLineFilter filter = new CutLineFilter();
        filter.setEnabled(true);
        filter.setCut("1-1");        
        assertNull(filter.filter(null));
    }
    
    
    /**
     * Tests the filter for empty input.
     */
    public void testFilterEmpty()
    {
        logger_.info("Running testFilterEmpty...");
        
        CutLineFilter filter = new CutLineFilter();
        filter.setEnabled(true);
        filter.setCut("1-1");        
        assertEquals("", filter.filter(""));
    }
    
    
    /**
     * Tests the filter for an invalid cut range.
     */
    public void testFilterInvalidRange()
    {
        logger_.info("Running testFilterInvalidRange...");
        
        CutLineFilter filter = new CutLineFilter();
        filter.setEnabled(true);
        
        // Begin is greater than end.
        try
        {
            String range = "4-2";
            filter.setCut(range);
            fail("Test should have failed for invalid range: " + range);
        }
        catch (IllegalArgumentException iae)
        {
            // Passed test.
            logger_.debug("Induced error: " + iae.getMessage());
        }
        
        // Begin is equal to end.
        try
        {
            String range = "5-4";
            filter.setCut(range);
            logger_.debug(StringUtil.addBars(filter.toString()));
            fail("Test should have failed for invalid range: " + range);
        }
        catch (IllegalArgumentException iae)
        {
            // Passed
            logger_.debug("Induced error: " + iae.getMessage());
        }
        
        // Not even a range
        try
        {
            String range = "this should fail";
            filter.setCut(range);
            logger_.debug(StringUtil.addBars(filter.toString()));
            fail("Test should have failed for invalid range: " + range);
        }
        catch (IllegalArgumentException iae)
        {
            // Passed
            logger_.debug("Induced error: " + iae.getMessage());
        }
    }

    
    /**
     * Tests setCut(null/empty) which should disable the filter.
     */
    public void testSetCutNullEmpty()
    {
        logger_.info("Running testSetCutNullEmpty...");
        
        CutLineFilter f = new CutLineFilter();
        f.setEnabled(true);
        f.setCut(null);
        assertTrue(!f.isEnabled());
        
        f.setEnabled(true);
        f.setCut("");
        assertTrue(!f.isEnabled());
    }
}