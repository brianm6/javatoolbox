package toolbox.plugin.jtail.filter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.plugin.jtail.filter.LineNumberDecorator}.
 */
public class LineNumberDecoratorTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(LineNumberDecoratorTest.class);
        
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
        TestRunner.run(LineNumberDecoratorTest.class);
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
        
        ILineFilter decorator = new LineNumberDecorator();
        decorator.setEnabled(true);
        
        for (int i = 0; i < 5000; i++)
        {  
            StringBuffer sb = new StringBuffer("Line");
            assertTrue(decorator.filter(sb));
            assertEquals("[" + (i + 1) + "] Line", sb.toString());
        }
            
    }
    
    
    /**
     * Tests the filter when disabled.
     */
    public void testFilterDisabled()
    {
        logger_.info("Running testFilterDisabled...");
        
        ILineFilter d = new LineNumberDecorator();
        d.setEnabled(false);
        StringBuffer sb = new StringBuffer("howdy");
        assertTrue(d.filter(sb));
        assertEquals("howdy", sb.toString());        
    }
}