package toolbox.util.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit test for ResourceCloser
 */
public class ResourceCloserTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceCloserTest.class);

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
        TestRunner.run(ResourceCloserTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testResourceCloser()
    {
        logger_.info("Running testResourceCloser...");
    }
}