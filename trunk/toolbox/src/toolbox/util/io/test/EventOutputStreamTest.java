package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.io.EventOutputStream;
import toolbox.util.io.NullOutputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for EventOutputStream
 */
public class EventOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(EventOutputStreamTest.class);
        
    /**
     * Entrypoint   
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(EventOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for EventOutputStreamTest
     * 
     * @param  arg0  Name
     */
    public EventOutputStreamTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
       
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        EventOutputStream cos = 
            new EventOutputStream(new NullOutputStream());
        
        assertNotNull(cos);
        assertEquals(0, cos.getCount());
    }
        
    /**
     * Tests the write(byte) method
     * 
     * @throws Exception on error
     */
    public void testWriteByte() throws Exception
    {
        logger_.info("Running testWriteByte...");
        
        StringOutputStream sos = new StringOutputStream();
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());        
        cos.write("x".getBytes()[0]);
        cos.flush();
        assertEquals("strings don't match", "x", sos.toString());
        assertEquals(1, cos.getCount());
    }
    
    /**
     * Tests the write(byte[]) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArray() throws Exception
    {
        logger_.info("Running testWriteByteArray...");

        StringOutputStream sos = new StringOutputStream();       
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes());
        cos.flush();
        assertEquals("strings don't match", "hello", sos.toString());
        assertEquals("hello".length(), cos.getCount());
    }
    
    /**
     * Tests the write(byte[], begin, len) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArraySubset() throws Exception
    {
        logger_.info("Running testWriteByteArraySubset...");

        StringOutputStream sos = new StringOutputStream();       
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes(), 2, 2);
        cos.flush();
        assertEquals("strings don't match", "ll", sos.toString());
        assertEquals(2, cos.getCount());
    }
    
    /**
     * Tests the event generation and listener notification
     *
     * @throws Exception on error
     */
    public void testListener() throws Exception
    {
        logger_.info("Running testListener...");
        
        EventOutputStream eos = new EventOutputStream(new NullOutputStream());
        OutputStreamListener listener = new OutputStreamListener();
        eos.addListener(listener);
        
        eos.write(1);
        assertEquals(1, listener.waitForWrite());
        assertEquals(1, eos.getCount());
        
        eos.flush();
        assertNotNull(listener.waitForFlush());
        
        eos.close();
        assertNotNull(listener.waitForClose());
    }

    //--------------------------------------------------------------------------
    // Inner classes 
    //--------------------------------------------------------------------------
   
    /** 
     * Listener used to make sure event notification is working correctly
     */    
    class OutputStreamListener implements EventOutputStream.Listener
    {
        private BlockingQueue writeQueue_ = new BlockingQueue();
        private BlockingQueue flushQueue_ = new BlockingQueue();
        private BlockingQueue closeQueue_ = new BlockingQueue();
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        public int waitForWrite() throws InterruptedException
        {
            return ((Integer) writeQueue_.pull()).intValue();
        }

        public EventOutputStream waitForFlush() throws InterruptedException
        {
            return (EventOutputStream) flushQueue_.pull();
        }

        public EventOutputStream waitForClose() throws InterruptedException
        {
            return (EventOutputStream) closeQueue_.pull();
        }

        //----------------------------------------------------------------------
        // EvenoutOutputStream.Listener Interface
        //----------------------------------------------------------------------
        
        public void byteWritten(EventOutputStream stream, int b)
        {
            try
            {
                writeQueue_.push(new Integer(b));
            }
            catch (InterruptedException e)
            {
                logger_.warn(e);
            }
        }
        
        public void streamClosed(EventOutputStream stream)
        {
            try
            {
                closeQueue_.push(stream);
            }
            catch (InterruptedException e)
            {
                logger_.warn(e);
            }
        }

        public void streamFlushed(EventOutputStream stream)
        {
            try
            {
                flushQueue_.push(stream);
            }
            catch (InterruptedException e)
            {
                logger_.warn(e);
            }
        }
    }
}