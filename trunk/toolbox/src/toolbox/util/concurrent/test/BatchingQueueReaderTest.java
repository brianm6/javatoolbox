package toolbox.util.concurrent.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BatchingQueueReader;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.concurrent.IBatchingQueueListener;

/**
 * Unit test for BatchingQueueReader
 */
public class BatchingQueueReaderTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(BatchingQueueReaderTest.class);

    private BlockingQueue batches_;

    /**
     * Entrypoint
     * 
     * @param  args  None
     */    
    public static void main(String[] args)
    {
        TestRunner.run(BatchingQueueReaderTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for BatchingQueueReaderTest
     * 
     * @param arg0  Name
     */
    public BatchingQueueReaderTest(String arg0)
    {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        batches_ = new BlockingQueue();        
    }

    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test notification
     * 
     * @throws Exception on error
     */
    public void testNextBatch() throws Exception
    {
        logger_.info("Running testNextBatch...");
        
        // Create queue and stuff with > 1 objects
        BlockingQueue q = new BlockingQueue();
        q.push("one");
        q.push("two");
        q.push("three");
        
        // Attaching batching reader and start
        BatchingQueueReader bqr = new BatchingQueueReader(q);
        IBatchingQueueListener queueListener = new TestQueueListener();
        bqr.addBatchingQueueListener(queueListener);
        bqr.start();
        
        // Pulling from the queue should return all elements as a single array
        Object[] batch = (Object[]) batches_.pull();
        
        // Test returned elements equal to pushed elements
        assertEquals(batch[0], "one");
        assertEquals(batch[1], "two");
        assertEquals(batch[2], "three");
        
        bqr.removeBatchingQueueListener(queueListener);
        bqr.stop();
    }

    /**
     * Test start() and stop()
     * 
     * @throws Exception on error
     */
    public void testStartStop() throws Exception
    {
        logger_.info("Running testStartStop...");
        
        BlockingQueue q = new BlockingQueue();
        q.push("one");
        q.push("two");
        q.push("three");
        
        BatchingQueueReader bqr = new BatchingQueueReader(q);
        IBatchingQueueListener queueListener = new TestQueueListener();
        bqr.addBatchingQueueListener(queueListener);
        bqr.start();
        
        Object[] batch = (Object[]) batches_.pull();
        
        assertEquals(batch[0], "one");
        assertEquals(batch[1], "two");
        assertEquals(batch[2], "three");
        
        bqr.stop();
        
        assertTrue(q.size() == 0);
        
        bqr.start();
        
        ThreadUtil.sleep(2000);
        
        bqr.stop();
        
        assertTrue(q.size() == 0);        
        
        q.push("three");
        q.push("four");
        q.push("five");
        
        logger_.info(q);
        
        bqr.start();
        
        Object[] batch2 = (Object[]) batches_.pull();
        
        assertEquals(batch2[0], "three");
        assertEquals(batch2[1], "four");
        assertEquals(batch2[2], "five");
        
        bqr.removeBatchingQueueListener(queueListener);
        bqr.stop();
    }

    /**
     * Test start() if called twice
     * 
     * @throws Exception on error
     */
    public void testStartTwice() throws Exception
    {
        logger_.info("Running testStartTwice...");
        
        BatchingQueueReader r = new BatchingQueueReader(new BlockingQueue());
        r.start();
        
        try
        {
            r.start();
            fail("Reader should be able to start if already running.");
        }
        catch (IllegalStateException ise)
        {
            assertTrue(true);
        }        
    }

    /**
     * Test stop() if called twice
     * 
     * @throws Exception on error
     */
    public void testStopTwice() throws Exception
    {
        logger_.info("Running testStopTwice...");
        
        BatchingQueueReader r = new BatchingQueueReader(new BlockingQueue());
        r.start();
        r.stop();
        
        try
        {
            r.stop();
            fail("Reader should be be able to be stopped twice");
        }
        catch (IllegalStateException ise)
        {
            assertTrue(true);
        }        
    }


    
    //--------------------------------------------------------------------------
    // Helper Classes
    //--------------------------------------------------------------------------
    
    class TestQueueListener implements IBatchingQueueListener
    {
        public void nextBatch(Object[] elements)
        {
            try
            {
                logger_.info("nextBatch: " + ArrayUtil.toString(elements));
                batches_.push(elements);    
            }
            catch (InterruptedException ie)
            {
                logger_.error("nextBatch", ie);
            }
        }
    }
}
