package toolbox.launcher.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.launcher.Main;

/**
 * Unit test for Main 
 */
public class MainTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(MainTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testMain()
    {
        logger_.info("Running testMain...");
        Main.main(new String[] {"showclasspath" });
    }

    public void testMainNegative()
    {
        logger_.info("Running testMainNegative...");
        Main.main(new String[] {"bogus_program" });
    }
}
