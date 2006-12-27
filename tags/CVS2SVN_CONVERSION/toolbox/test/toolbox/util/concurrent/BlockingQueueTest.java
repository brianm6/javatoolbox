package toolbox.util.concurrent;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.ThreadUtil;

/**
 * Unit test for {@link toolbox.util.concurrent.BlockingQueue}.
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
     * @param args None recognized.
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
     * @throws Exception on error.
     */
    public void testPullTimeoutExpired() throws Exception 
    {
        logger_.info("Running testPullTimeoutExpired...");
        
        BlockingQueue q = new LinkedBlockingQueue();
        ElapsedTime time = new ElapsedTime();
        Object obj = q.poll(5000, TimeUnit.MILLISECONDS);
        time.setEndTime();
        
        logger_.debug("Elapsed time = " + time);
        logger_.debug("Popped obj   = " + obj);
    }
    
    
    /**
     * Test pull() when item is popped before the timeout.
     * 
     * @throws Exception on error.
     */
    public void testPullTimeoutMet() throws Exception 
    {
        logger_.info("Running testPullTimeoutMet...");
        
        final BlockingQueue q = new LinkedBlockingQueue();
        
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                ElapsedTime time = new ElapsedTime();            
                Object obj = null;
                
                try
                {
                    obj = q.poll(10000, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) 
                {
                    ;   // Ignore
                }
                
                time.setEndTime();
                
                logger_.debug("Elapsed time = " + time);
                logger_.debug("Popped obj   = " + obj);                
            }
        }, "testPullTimeoutMet");
        t.start();
                
        ThreadUtil.sleep(2000);
        
        q.offer("moo");
        
        t.join();
    }
    
    
    /**
     * Tests toString().
     * 
     * @throws Exception on error.
     */
    public void testToString() throws Exception
    {
        logger_.info("Running testToString...");
        
        BlockingQueue q = new LinkedBlockingQueue();
        
        q.offer("a");
        q.offer("b");
        q.offer("c");
        
        logger_.debug("toString = " + q);
    }
    
    
    /**
     * Tests interrupt() while blocked.
     * 
     * @throws Exception on error.
     */
    public void testInterrupt() throws Exception
    {
        logger_.info("Running testInterrupt...");
        
        final BlockingQueue q = new LinkedBlockingQueue();
        
        Thread blocked = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    q.poll();
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