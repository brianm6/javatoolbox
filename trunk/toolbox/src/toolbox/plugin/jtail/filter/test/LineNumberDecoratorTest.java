package toolbox.jtail.filter.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.jtail.filter.LineNumberDecorator;

/**
 * Unit test for LineNumberDecorator.
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
        
        LineNumberDecorator decorator = new LineNumberDecorator();
        decorator.setEnabled(true);
        
        for (int i = 0; i < 5000; i++)
            assertEquals("[" + (i + 1) + "] Line", decorator.filter("Line"));
    }
    
    
    /**
     * Tests the filter when disabled.
     */
    public void testFilterDisabled()
    {
        logger_.info("Running testFilterDisabled...");
        
        LineNumberDecorator d = new LineNumberDecorator();
        d.setEnabled(false);
        assertEquals("howdy", d.filter("howdy"));        
    }
    
    
    /**
     * Tests the filter for null input.
     */
    public void testFilterNull()
    {
        logger_.info("Running testFilterNull...");
        
        LineNumberDecorator d = new LineNumberDecorator();
        d.setEnabled(true);
        assertNull(d.filter(null)); 
    }
}