package toolbox.findclass.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.findclass.FindClass;
import toolbox.findclass.FindClassResult;
import toolbox.util.ArrayUtil;

/**
 * Unit test for Findclass.
 */
public class FindClassTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FindClassTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Test entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FindClassTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test finding a system class.
     * 
     * @throws Exception on error
     */
    public void testFindClassSystem() throws Exception
    {
        logger_.info("Running testFindClassSystem...");
        
        String searchFor = "java.lang.Object";
        FindClass finder = new FindClass();
        
        String results = 
            ArrayUtil.toString(
                finder.findClass(searchFor, false), true);

        logger_.info("Results: " + results);
        assertTrue("Couldn't find " + searchFor, results.indexOf(searchFor)>=0);
    }
    
    
    /**
     * Test class not found.
     * 
     * @throws Exception on error
     */
    public void testFindClassNotFound() throws Exception
    {
        logger_.info("Running testFindClassNotFound...");
        
        String searchFor = "java.lang.Bogus";
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);
        assertEquals("Class should not have been found", 0, results.length);
    }

    
    /**
     * Test case sensetivity.
     * 
     * @throws Exception on error
     */
    public void testFindClassCaseSensetivity() throws Exception
    {
        logger_.info("Running testFindClassCaseSensetivity...");
        
        String searchFor = "java.lang.object"; // <== o is lowercase
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);
        assertEquals("Class should not have been found", 0, results.length);
        
        results = finder.findClass(searchFor, true); // Ignore case
        assertEquals("Class should have been found", 1, results.length);
    }
    
    
    /**
     * Test finding an archive class.
     * 
     * @throws Exception on error
     */
    public void xtestFindClassInArchive() throws Exception
    {
        logger_.info("Running testFindClassInArchive...");
        
        String searchFor = getClass().getName();
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass(searchFor, false);

        logger_.info("Results: " + ArrayUtil.toString(results));
        assertEquals("Couldn't find " + searchFor, 1, results.length);
    }
}
