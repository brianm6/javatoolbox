package toolbox.junit;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junitx.util.DirectorySuiteBuilder;
import junitx.util.TestFilter;

import org.apache.log4j.Logger;

/**
 * A test suite that includes only those unit tests suitable for execution under
 * Clover. This excludes all user interface and non-stand alone unit tests
 * explicitly.
 */
public class CloverTestSuite extends TestSuite
{
    private static final Logger logger_ = 
        Logger.getLogger(CloverTestSuite.class);
 
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     * @throws Exception on error.
     */
    public static void main(String[] args) throws Exception
    {
        TestRunner.run(suite());
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Returns a test suite containing all the unit tests able to be run under
     * Clover.
     * 
     * @return Test
     * @throws Exception on error.
     */
    public static Test suite() throws Exception
    {
        TestFilter cloverFilter = new CloverTestFilter();
        DirectorySuiteBuilder builder = new DirectorySuiteBuilder(cloverFilter);
        
        // TODO: This is kinda iffy and assumes were in the root directory 
        //       of the project.
        Test test = builder.suite("classes");
        
        logger_.info("Total Clover test cases: " + test.countTestCases());
        return test;
    }
}
