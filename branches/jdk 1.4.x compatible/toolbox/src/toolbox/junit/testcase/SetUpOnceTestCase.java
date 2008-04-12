package toolbox.junit.testcase;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Template for a unit test that is able to run setup and teardown once for an
 * entire suite.
 */
public class SetUpOnceTestCase extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SetUpOnceTestCase.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entry point.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        logger_.info("Running main...");
        TestRunner.run(suite());
    }

    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------
    
    /**
     * Wraps suite in a TestSetup.
     * 
     * @return Test
     */
    public static Test suite()
    {
        logger_.info("Running suite...");
        
        TestSuite suite = new TestSuite(SetUpOnceTestCase.class);
        
        TestSetup wrapper = new TestSetup(suite)
        {
            protected void setUp() throws Exception
            {
                setUpOnce();
            }

            protected void tearDown() throws Exception
            {
                tearDownOnce();            
            }
        };
        
        return wrapper;
    }
    
    
    /**
     * Setup that is run only once at the beginning of this entire suite. 
     */
    private static void setUpOnce()
    {
        // Your one time setup code goes here
        logger_.info("Running setUpOnce...");
    }

    
    /**
     * Teardown that is run only once at the end of this entire suite. 
     */
    private static void tearDownOnce()
    {
        // Your one time tear down code goes here
        logger_.info("Running tearDownOnce...");
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Your once per test method setup code goes here
        logger_.info("Running setUp...");
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        // Your once per test method teardown code goes here
        logger_.info("Running tearDown...");
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testMethod1()
    {
        logger_.info("Running testMethod1...");
    }
    
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testMethod2()
    {
        logger_.info("Running testMethod2...");
    }
}