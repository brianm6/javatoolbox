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
     * Test invalid option.
     */
    public void testPrintUsage()
    {
        logger_.info("Running testPrintUsage...");
        
        Main.main(new String[] {"-badflag"});
        Main.main(new String[] {"-h"});
        Main.main(new String[] {"-?"});
    }
    
    
    /**
     * Test show targets option.
     * 
     * @throws Exception on error.
     */
    public void testFindShowTargetsOption() throws Exception
    {
        logger_.info("Running testFindShowTargetsOption...");
        
        Main.main(new String[] {"-t", "xxx"});
    }
    
    
    /**
     * Test case sensetive option.
     * 
     * @throws Exception on error.
     */
    public void testFindCaseOption() throws Exception
    {
        logger_.info("Running testFindShowTargets...");
        
        Main.main(new String[] {"-c", "XYZ"});
    }
}
