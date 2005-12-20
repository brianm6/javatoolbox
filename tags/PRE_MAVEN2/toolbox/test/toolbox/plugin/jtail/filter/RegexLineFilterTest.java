package toolbox.plugin.jtail.filter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.plugin.jtail.filter.RegexLineFilter}.
 */
public class RegexLineFilterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(RegexLineFilterTest.class);
        
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------

    /**
     * Entrypoint
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(RegexLineFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests filter()
     * 
     * @throws Exception on error.
     */
    public void testFilter() throws Exception
    {
        logger_.info("Running testFilter...");
        
        RegexLineFilter filter = new RegexLineFilter();
        filter.setEnabled(true);
        String original = "abcdef";
        StringBuffer str = new StringBuffer(original);
        
        // Match - case insensetive
        filter.setRegularExpression("abc");
        assertTrue(filter.filter(str));
        assertEquals(original, str.toString());
        
        // No match - case insensetive
        filter.setRegularExpression("xyz");
        assertFalse(filter.filter(str));
        assertEquals(original, str.toString());
        
        // Match case - case sensetive
        filter.setMatchCase(true);
        filter.setRegularExpression("abc");        
        assertTrue(filter.filter(str));
        assertEquals(original, str.toString());
        
        // No match - case sensetive 
        filter.setRegularExpression("ABC");
        assertFalse(filter.filter(str));
        assertEquals(original, str.toString());
    }
    
    
    /**
     * Tests the filter when disabled.
     */
    public void testFilterDisabled()
    {
        logger_.info("Running testFilterDisabled...");
        
        RegexLineFilter d = new RegexLineFilter(".*");
        d.setEnabled(false);
        StringBuffer sb = new StringBuffer("howdy");
        assertTrue(d.filter(sb));        
        assertEquals("howdy", sb.toString());
    }
}