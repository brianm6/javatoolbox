package toolbox.findclass.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.findclass.Main;

/**
 * Unit test for Main
 */
public class MainTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(MainTest.class);
    
    /**
     * Test entry point
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Arg constructor
     * 
     * @param arg  Args
     */
    public MainTest(String arg)
    {
        super(arg);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test finding a class in a jarfile
     * 
     * @throws Exception on error
     */
    public void testFindInJar() throws Exception
    {
        logger_.info("Running testFindInJar...");
        
        Main.main(new String[] {  "filter$" } );
    }
    
    /**
     * Tests printUsage()
     */
    public void testPrintUsage()
    {
    	logger_.info("Running testPrintUsage...");
    	
    	Main.main(new String[] { "-badflag"});
    }
}
