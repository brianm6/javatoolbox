package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.AssertionException;

/**
 * Unit test for AssertionException
 */
public class AssertionExceptionTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AssertionExceptionTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(AssertionExceptionTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests default constructor
     */
    public void testDefaultConstructor()
    {
        logger_.info("Running testDefaultConstructor...");
        
        assertNotNull(new AssertionException());
    }    
    
    /**
     * Tests arg constructor
     */
    public void testArgConstructor()
    {
        logger_.info("Running testArgConstructor...");

        assertNotNull(new AssertionException("arg"));        
    }    
}