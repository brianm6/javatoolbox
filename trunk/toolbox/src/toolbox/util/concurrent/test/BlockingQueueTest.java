package toolbox.util.concurrent.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;

/**
 * Unit test for BlockingQueue
 */
public class BlockingQueueTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(BlockingQueueTest.class);

    /**
     * Entrypoint
     */    
    public static void main(String[] args)
    {
        TestRunner.run(BlockingQueueTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for BlockingQueueTest.
     * 
     * @param arg0
     */
    public BlockingQueueTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test pull() when the timeout expires
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
     * Test pull() when item is popped before the timeout
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
                catch(InterruptedException e) {}
                
                time.setEndTime();
                
                logger_.info("Elapsed time: " + time);
                logger_.info("Popped obj  : " + obj);                
            }
        });
        t.start();
                
        ThreadUtil.sleep(2000);
        
        q.push("moo");
        
        t.join();
    }
    
}
