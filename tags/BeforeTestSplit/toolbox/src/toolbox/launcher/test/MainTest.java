package toolbox.launcher.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.launcher.Main;

/**
 * Unit test for Main.
 */
public class MainTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(MainTest.class);
    
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
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests positive launch.
     */
    public void testMain()
    {
        logger_.info("Running testMain...");
        Main.main(new String[] {"showclasspath" });
    }

    
    /**
     * Tests positive launch passing an arg to the program.
     */
    public void testMainPassArg()
    {
        logger_.info("Running testMainPassArg...");
        Main.main(new String[] {"banner", "!@#$%"});
    }
    
    
    /**
     * Tests printing help.
     */
    public void testMainHelp()
    {
        logger_.info("Running testMainHelp...");
        Main.main(new String[0]);
    }
    
    
    /**
     * Tests failed launch.
     */
    public void testMainNegative()
    {
        logger_.info("Running testMainNegative...");
        Main.main(new String[] {"bogus_program"});
    }
}