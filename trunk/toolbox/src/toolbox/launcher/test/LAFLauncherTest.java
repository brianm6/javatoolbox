package toolbox.launcher.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.launcher.LAFLauncher;

/**
 * Unit test for LAFLauncher
 */
public class LAFLauncherTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(LAFLauncherTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(LAFLauncherTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests main() with no args for the target class
     */
    public void testMainNoArgs()
    {
        logger_.info("Running testMainNoArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] { target });
    }
    
    /**
     * Tests main() with one args for the target class
     */
    public void testMainOneArg()
    {
        logger_.info("Running testMainOneArg...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] { target, "showclasspath" });
    }

    /**
     * Tests main() with > 1 args
     */
    public void testMainManyArgs()
    {
        logger_.info("Running testMainManyArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(
            new String[] { target, "banner", "testing", "LAFLauncher"});
    }
    
    /**
     * Tests printUsage()
     */
    public void testPrintUsage()
    {
        logger_.info("Running testPrintUsage...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[0]);
    }
}
