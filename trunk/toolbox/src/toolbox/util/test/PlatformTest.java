package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Platform;

/**
 * Unit test for Platform
 */
public class PlatformTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(PlatformTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(PlatformTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests hasJava14()
     */
    public void testHasJava14()
    {
        logger_.info("Running testHasJava14...");
        logger_.info("Has Java 1.4: " + Platform.hasJava14());
    }


    /**
     * Tests isDOSDerived()
     */
    public void testIsDOSDerived()
    {
        logger_.info("Running testIsDOSDerived...");
        logger_.info("Is DOS derived: " + Platform.isDOSDerived());
    }


    /**
     * Tests isWindows()
     */
    public void testIsWindows()
    {
        logger_.info("Running testIsWindows...");
        logger_.info("Is Windows: " + Platform.isWindows());
    }


    /**
     * Tests isWindows9x()
     */
    public void testIsWindows9x()
    {
        logger_.info("Running testIsWindows9x...");
        logger_.info("Is Windows 9x: " + Platform.isWindows9x());
    }


    /**
     * Tests isWindowsNT()
     */
    public void testIsWindowsNT()
    {
        logger_.info("Running testIsWindowsNT...");
        logger_.info("Is Windows NT: " + Platform.isWindowsNT());
    }


    /**
     * Tests isOS2()
     */
    public void testIsOS2()
    {
        logger_.info("Running testIsOS2...");
        logger_.info("Is OS/2: " + Platform.isOS2());
    }


    /**
     * Tests isUnix()
     */
    public void testIsUnix()
    {
        logger_.info("Running testIsUnix...");
        logger_.info("Is Unix: " + Platform.isUnix());
    }


    /**
     * Tests isMacOS()
     */
    public void testIsMacOS()
    {
        logger_.info("Running testIsMacOS...");
        logger_.info("Is Mac OS: " + Platform.isMacOS());
    }
}