package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit Test for {@link toolbox.util.BrowserLauncher}.
 */
public class BrowserLauncherTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(BrowserLauncherTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(BrowserLauncherTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests openURL() on a page at yahoo.com.
     */
    public void testOpenURL() throws Exception
    {
        logger_.info("Running testOpenURL...");
        
        BrowserLauncher.openURL("http://www.yahoo.com");
    }
}