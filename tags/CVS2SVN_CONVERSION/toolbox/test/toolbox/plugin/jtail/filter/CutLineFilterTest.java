package toolbox.plugin.jtail.filter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * Unit test for {@link toolbox.plugin.jtail.filter.CutLineFilter}.
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
        StringBuffer sb = new StringBuffer("12345");
        assertTrue(filter.filter(sb));
        assertEquals("2345", sb.toString());
        
        // First char + subset
        sb = new StringBuffer("12345");
        filter.setCut("1-2");
        assertTrue(filter.filter(sb));
        assertEquals("345", sb.toString());
        
        // All
        sb = new StringBuffer("12345");
        filter.setCut("1-5");
        assertTrue(filter.filter(sb));
        assertEquals("", sb.toString());
        
        // Subset
        sb = new StringBuffer("12345");
        filter.setCut("2-4");
        assertTrue(filter.filter(sb));
        assertEquals("15", sb.toString());
        
        // Last char
        sb = new StringBuffer("12345");
        filter.setCut("5-5");
        assertTrue(filter.filter(sb));
        assertEquals("1234", sb.toString());
    
        // Last char + subset
        sb = new StringBuffer("12345");
        filter.setCut("3-5");
        assertTrue(filter.filter(sb));
        assertEquals("12", sb.toString());
        
        // Out of range
        sb = new StringBuffer("12345");
        filter.setCut("20-30");
        assertTrue(filter.filter(sb));
        assertEquals("12345", sb.toString());
        
        // Overlapping out of range
        sb = new StringBuffer("12345");
        filter.setCut("3-30");
        assertTrue(filter.filter(sb));
        assertEquals("12", sb.toString());
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
        StringBuffer sb = new StringBuffer(s);
        assertTrue(filter.filter(sb));
        assertEquals(s, sb.toString());
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
        StringBuffer sb = new StringBuffer("");
        assertTrue(filter.filter(sb));
        assertEquals("", sb.toString());
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
            logger_.debug(StringUtil.banner(filter.toString()));
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
            logger_.debug(StringUtil.banner(filter.toString()));
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