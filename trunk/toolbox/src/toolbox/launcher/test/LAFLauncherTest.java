package toolbox.launcher.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.StandaloneTestCase;
import toolbox.launcher.LAFLauncher;

/**
 * Unit test for LAFLauncher.
 */
public class LAFLauncherTest extends TestCase implements StandaloneTestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(LAFLauncherTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(LAFLauncherTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests main() with no args for the target class.
     * 
     * @throws Exception on error 
     */
    public void testMainNoArgs() throws Exception
    {
        logger_.info("Running testMainNoArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] {target});
    }
    
    
    /**
     * Tests main() with one args for the target class.
     * 
     * @throws Exception on error 
     */
    public void testMainOneArg() throws Exception
    {
        logger_.info("Running testMainOneArg...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] {target, "showclasspath"});
    }


    /**
     * Tests main() with > 1 args.
     * 
     * @throws Exception on error 
     */
    public void testMainManyArgs() throws Exception
    {
        logger_.info("Running testMainManyArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(
            new String[] {target, "banner", "testing", "LAFLauncher"});
    }
    
    
    /**
     * Tests printUsage()
     * 
     * @throws Exception on error 
     */
    public void testPrintUsage() throws Exception
    {
        logger_.info("Running testPrintUsage...");
        
        LAFLauncher.main(new String[0]);
    }
}