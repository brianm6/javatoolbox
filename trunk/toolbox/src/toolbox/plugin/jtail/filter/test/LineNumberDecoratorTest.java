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
        
        for (int i=0; i<5000; i++)
            assertEquals("[" + (i+1) + "] Line", decorator.filter("Line"));
    }
}