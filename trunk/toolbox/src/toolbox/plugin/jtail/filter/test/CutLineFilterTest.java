package toolbox.jtail.filter.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.jtail.filter.CutLineFilter;

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
    }
}