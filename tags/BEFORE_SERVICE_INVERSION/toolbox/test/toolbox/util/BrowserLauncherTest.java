package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit Test for BrowserLauncher.
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
    
    public void testOpenURL() throws Exception
    {
        BrowserLauncher.openURL("http://www.yahoo.com");
    }
}
