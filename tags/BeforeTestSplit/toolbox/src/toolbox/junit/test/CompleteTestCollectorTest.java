package toolbox.junit.test;

import java.util.Enumeration;

import junit.framework.TestCase;
import junit.runner.TestCollector;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.CompleteTestCollector;

/**
 * Unit test for CompleteTestCollector.
 */
public class CompleteTestCollectorTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(CompleteTestCollectorTest.class);

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
        TestRunner.run(CompleteTestCollectorTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests collectTests()
     */
    public void testCollectTests()
    {
        logger_.info("Running testCollectTests...");
        
        TestCollector tc = new CompleteTestCollector();
        
        for (Enumeration e = tc.collectTests(); e.hasMoreElements();)
        {
            String classname = (String) e.nextElement();

            logger_.info("Testclass=" + classname);

            assertTrue(
                classname + " should end with Test",
                classname.endsWith("Test"));
        }
    }
}