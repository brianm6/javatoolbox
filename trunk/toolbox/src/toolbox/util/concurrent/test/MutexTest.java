package toolbox.util.concurrent.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.Mutex;

/**
 * Unit test for Mutex
 */
public class MutexTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(MutexTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MutexTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for MutexTest.
     * 
     * @param arg0 Test name
     */
    public MutexTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests acquire()
     * 
     * @throws Exception on error
     */
    public void testAcquire() throws InterruptedException
    {
        logger_.info("Running testAcquire...");
        
        Mutex m = new Mutex();
        m.acquire();
        m.release();
        m.acquire();
    }

    /**
     * Tests release()
     * 
     * @throws Exception on error
     */
    public void testRelease() throws InterruptedException
    {
        logger_.info("Running testRelease...");
        
        Mutex m = new Mutex();
        m.release();
        m.acquire();
        m.release();
    }

    /**
     * Tests attempt()
     * 
     * @throws Exception on error
     */
    public void testAttempt() throws InterruptedException
    {
        logger_.info("Running testAttempt...");
        
        Mutex m = new Mutex();
        assertTrue(m.attempt(1000));
    }
}
