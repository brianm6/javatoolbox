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
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(AssertionExceptionTest.class);

    /**
     * Entrypoint
     * 
     * @param args Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(AssertionExceptionTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
            
    /**
     * Constructor for AssertionExceptionTest.
     * 
     * @param  arg0  Test name
     */
    public AssertionExceptionTest(String arg0)
    {
        super(arg0);
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