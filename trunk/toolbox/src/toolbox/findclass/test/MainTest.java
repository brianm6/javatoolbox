package toolbox.findclass.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.findclass.Main;

/**
 * Unit test for Main.
 */
public class MainTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(MainTest.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Test entry point.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test finding a class in a jarfile.
     * 
     * @throws Exception on error.
     */
    public void testFindInJar() throws Exception
    {
        logger_.info("Running testFindInJar...");
        
        Main.main(new String[] {"filter$"});
    }
    
    
    /**
     * Tests printUsage().
     */
    public void testPrintUsage()
    {
        logger_.info("Running testPrintUsage...");
        
        Main.main(new String[] {"-badflag"});
    }
}
