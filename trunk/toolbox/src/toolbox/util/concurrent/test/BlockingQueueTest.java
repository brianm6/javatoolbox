package toolbox.util.concurrent.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;

/**
 * Unit test for BlockingQueue.
 */
public class BlockingQueueTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(BlockingQueueTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */    
    public static void main(String[] args)
    {
        TestRunner.run(BlockingQueueTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test pull() when the timeout expires.
     * 
     * @throws Exception on error
     */
    public void testPullTimeoutExpired() throws Exception 
    {
        logger_.info("Running testPullTimeoutExpired...");
        
        BlockingQueue q = new BlockingQueue();
        ElapsedTime time = new ElapsedTime();
        Object obj = q.pull(5000);
        time.setEndTime();
        
        logger_.info("Elapsed time: " + time);
        logger_.info("Popped obj  : " + obj);
        
    }
    
    
    /**
     * Test pull() when item is popped before the timeout.
     * 
     * @throws Exception on error
     */
    public void testPullTimeoutMet() throws Exception 
    {
        logger_.info("Running testPullTimeoutMet...");
        
        final BlockingQueue q = new BlockingQueue();
        
        Thread t = new Thread( new Runnable()
        {
            public void run()
            {
                ElapsedTime time = new ElapsedTime();            
                Object obj = null;
                
                try
                {
                    obj = q.pull(10000);
                }
                catch(InterruptedException e) 
                {
                    ;   // Ignore
                }
                
                time.setEndTime();
                
                logger_.info("Elapsed time: " + time);
                logger_.info("Popped obj  : " + obj);                
            }
        }, "testPullTimeoutMet");
        t.start();
                
        ThreadUtil.sleep(2000);
        
        q.push("moo");
        
        t.join();
    }
    
    
    /**
     * Tests toString().
     * 
     * @throws Exception on error
     */
    public void testToString() throws Exception
    {
        logger_.info("Running testToString...");
        
        BlockingQueue q = new BlockingQueue();
        
        q.push("a");
        q.push("b");
        q.push("c");
        
        logger_.info("toString: " + q);
    }
    
    
    /**
     * Tests interrupt() while blocked.
     * 
     * @throws Exception on error
     */
    public void testInterrupt() throws Exception
    {
        logger_.info("Running testInterrupt...");
        
        final BlockingQueue q = new BlockingQueue();
        
        Thread blocked = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    q.pull();
                }
                catch (Throwable t)
                {
                    logger_.debug("Caught in run() " + t);
                }
            }
        });
        
        blocked.start();
        
        ThreadUtil.sleep(1000);
        blocked.interrupt();
        logger_.debug("\n" + ThreadUtil.toString(blocked));
        
        assertTrue(!blocked.isAlive());
    }
}