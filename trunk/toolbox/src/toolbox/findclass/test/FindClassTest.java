package toolbox.findclass.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.findclass.FindClass;
import toolbox.findclass.FindClassResult;
import toolbox.util.ArrayUtil;

/**
 * Unit test for findclass
 */
public class FindClassTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FindClassTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Test entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FindClassTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test finding a class in a jarfile
     * 
     * @throws  Exception on error
     */
    public void testFindInJar() throws Exception
    {
        logger_.info("Running testFindInJar...");
        
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass("Info$", false);
        
        logger_.info("\n" + ArrayUtil.toString(results, true));
    }

}
