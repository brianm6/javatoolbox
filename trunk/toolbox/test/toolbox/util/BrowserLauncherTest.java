package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit Test for {@link toolbox.util.BrowserLauncher}.
 */
public class BrowserLauncherTest extends TestCase
{
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
        BrowserLauncher.openURL("http://www.yahoo.com");
    }
}