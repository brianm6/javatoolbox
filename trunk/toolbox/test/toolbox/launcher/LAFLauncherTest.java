package toolbox.launcher;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.StandaloneTestCase;

/**
 * Unit test for {@link toolbox.launcher.LAFLauncher}.
 */
public class LAFLauncherTest extends TestCase implements StandaloneTestCase
{
    private static final Logger logger_ = Logger.getLogger(LAFLauncherTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(LAFLauncherTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testMainNoArgs() throws Exception
    {
        logger_.info("Running testMainNoArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] {target});
    }
    
    
    public void testMainOneArg() throws Exception
    {
        logger_.info("Running testMainOneArg...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] {target, "showclasspath"});
    }


    public void testMainManyArgs() throws Exception
    {
        logger_.info("Running testMainManyArgs...");
        
        String target = "toolbox.launcher.Main";
        LAFLauncher.main(new String[] {target, "banner", "testing", "LAFLauncher"});
    }
    
    
    public void testPrintUsage() throws Exception
    {
        logger_.info("Running testPrintUsage...");
        
        LAFLauncher.main(new String[0]);
    }
}