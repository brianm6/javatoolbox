package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * Unit test for ExceptionUtil.
 */
public class ExceptionUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ExceptionUtilTest.class);

    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(ExceptionUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the getStackTrace() method.
     */
    public void testGetStackTrace()
    {
        logger_.info("Running testGetStackTrace...");
        
        Exception e = new Exception("wumba");
        String stackTrace = ExceptionUtil.getStackTrace(e);
        assertNotNull(stackTrace);
        logger_.info(stackTrace);        
    }
}
