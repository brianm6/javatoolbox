package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Logger;
import toolbox.util.ExceptionUtil;

/**
 * Unit test for ExceptionUtil
 */
public class ExceptionUtilTest extends TestCase
{
    
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(ExceptionUtilTest.class);

    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(ExceptionUtilTest.class);
    }

    /**
     * Create
     */
    public ExceptionUtilTest(String name)
    {
        super(name);
    }
    
    /**
     * Tests the getStackTrace() method
     */
    public void testGetStackTrace()
    {
        Exception e = new Exception("wumba");
        String stackTrace = ExceptionUtil.getStackTrace(e);
        assertNotNull(stackTrace);
        logger_.info(stackTrace);        
    }
}
