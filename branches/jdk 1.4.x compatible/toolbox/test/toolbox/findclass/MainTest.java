package toolbox.findclass;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.findclass.Main}.
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
    
    public void testMain_FindInJar() throws Exception
    {
        logger_.info("Running testMain_FindInJar ...");
        Main.main(new String[] {"filter$"});
    }
    
    
    public void testMain_HelpOption()
    {
        logger_.info("Running testMain_HelpOption ...");
        Main.main(new String[] {"-h"});
    }
    
    
    public void testMain_ShowTargetsOption() throws Exception
    {
        logger_.info("Running testMain_ShowTargetsOption ...");
        Main.main(new String[] {"-t", "xxx"});
    }
    
    
    public void testMain_CaseSensetiveOption() throws Exception
    {
        logger_.info("Running testMain_CaseSensetiveOption ...");
        Main.main(new String[] {"-c", "XYZ"});
    }
    
    public void testMain_NoArguments()
    {
        logger_.info("Running testMain_NoArguments ...");
        Main.main(new String[0]);
    }
}